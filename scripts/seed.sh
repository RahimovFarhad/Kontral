#!/usr/bin/env bash
#
# seed.sh — Populate the Job_Post backend with test data for manual testing.
#
# Registers 10 accounts (5 "employers", 5 "employees"), fills in their
# profiles, sets their preferred role, and creates several posts each:
#   - employers  -> JOB_REQUEST posts   (POST /api/v1/posts/create)
#   - employees  -> SERVICE_OFFER posts (POST /api/v1/service-posts/create)
#
# Everything goes through the real HTTP API, so it exercises the same code
# paths a browser would. Requires: bash, curl, jq.
#
# Usage:
#   ./scripts/seed.sh                 # targets http://localhost:8080
#   BASE_URL=http://localhost:8080 ./scripts/seed.sh
#   PASSWORD='MyPass123!' ./scripts/seed.sh
#
# Notes:
#   * The app must be running (e.g. with the `dev` profile) before you run this.
#   * Re-running is safe: registration of an existing email/phone is reported
#     and skipped, then the script logs in with the known password and continues.
#   * All accounts share the same password (default below) so testing is easy.

set -uo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
PASSWORD="${PASSWORD:-Password123!}"

# ----------------------------------------------------------------------------
# Small helpers
# ----------------------------------------------------------------------------
log()  { printf '\033[1;34m[seed]\033[0m %s\n' "$*"; }
ok()   { printf '  \033[1;32m✓\033[0m %s\n' "$*"; }
warn() { printf '  \033[1;33m!\033[0m %s\n' "$*"; }
err()  { printf '  \033[1;31m✗\033[0m %s\n' "$*"; }

require() {
  command -v "$1" >/dev/null 2>&1 || { err "missing required tool: $1"; exit 1; }
}
require curl
require jq

# Preflight: is the server reachable?
if ! curl -fsS -o /dev/null "$BASE_URL/api/v1/posts/all" 2>/dev/null; then
  err "Cannot reach $BASE_URL — is the backend running?"
  err "Start it (dev profile) and retry, e.g.:  SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run"
  exit 1
fi
log "Target: $BASE_URL"

# Register a user. Idempotent-ish: an already-registered email is fine.
# Args: email password phone
register() {
  local email="$1" password="$2" phone="$3"
  local body resp code
  body=$(jq -nc --arg e "$email" --arg p "$password" --arg ph "$phone" \
    '{firstName:"", lastName:"", email:$e, password:$p, phoneNumber:$ph}')
  resp=$(curl -sS -w '\n%{http_code}' -X POST "$BASE_URL/api/v1/user/register" \
    -H 'Content-Type: application/json' -d "$body")
  code=$(printf '%s' "$resp" | tail -n1)
  if [[ "$code" == "200" ]]; then
    ok "registered $email"
  else
    warn "register $email -> HTTP $code (likely already exists; continuing)"
  fi
}

# Authenticate and echo the bearer token (empty string on failure).
# Args: email password
login() {
  local email="$1" password="$2" body resp code token
  body=$(jq -nc --arg e "$email" --arg p "$password" '{email:$e, password:$p}')
  resp=$(curl -sS -w '\n%{http_code}' -X POST "$BASE_URL/api/v1/user/authenticate" \
    -H 'Content-Type: application/json' -d "$body")
  code=$(printf '%s' "$resp" | tail -n1)
  if [[ "$code" != "200" ]]; then
    return 1
  fi
  token=$(printf '%s' "$resp" | sed '$d' | jq -r '.token // empty')
  printf '%s' "$token"
}

# Set the user's preferred role. Args: token role(EMPLOYER|EMPLOYEE|All)
set_preferred_role() {
  local token="$1" role="$2"
  curl -sS -o /dev/null -X PUT "$BASE_URL/api/v1/user/preferred-role" \
    -H "Authorization: Bearer $token" -H 'Content-Type: application/json' \
    -d "$(jq -nc --arg r "$role" '{preferredRole:$r}')"
}

# Update profile fields. Args: token nickName aboutMe linkedIn preferredRole
update_profile() {
  local token="$1" nick="$2" about="$3" linkedin="$4" role="$5"
  curl -sS -o /dev/null -X PUT "$BASE_URL/api/v1/user/updateMe" \
    -H "Authorization: Bearer $token" -H 'Content-Type: application/json' \
    -d "$(jq -nc --arg n "$nick" --arg a "$about" --arg l "$linkedin" --arg r "$role" \
      '{nickName:$n, aboutMe:$a, linkedIn:$l, preferredRole:$r}')"
}

# Create a post. Args: token endpoint json-body
create_post() {
  local token="$1" endpoint="$2" body="$3" resp code
  resp=$(curl -sS -w '\n%{http_code}' -X POST "$BASE_URL$endpoint" \
    -H "Authorization: Bearer $token" -H 'Content-Type: application/json' -d "$body")
  code=$(printf '%s' "$resp" | tail -n1)
  if [[ "$code" == "200" ]]; then
    local title
    title=$(printf '%s' "$body" | jq -r '.title')
    ok "post: $title"
  else
    err "post failed (HTTP $code): $(printf '%s' "$resp" | sed '$d' | head -c 200)"
  fi
}

# ----------------------------------------------------------------------------
# Account roster — 5 employers, 5 employees
# Fields (|-separated): email | phone | nickName | preferredRole | aboutMe | linkedIn
# ----------------------------------------------------------------------------
USERS=(
  "alice.morgan@example.com|+15550101001|alice_hires|EMPLOYER|Hiring manager at a fast-growing fintech. Always looking for sharp engineers.|https://linkedin.com/in/alice-morgan"
  "ben.carter@example.com|+15550101002|ben_builds|EMPLOYER|Founder of a small product studio. We ship web apps for startups.|https://linkedin.com/in/ben-carter"
  "carla.diaz@example.com|+15550101003|carla_co|EMPLOYER|Operations lead at a logistics company. Recruiting across ops and support.|https://linkedin.com/in/carla-diaz"
  "david.kim@example.com|+15550101004|david_recruits|EMPLOYER|Tech recruiter specializing in mobile and backend roles.|https://linkedin.com/in/david-kim"
  "emma.wright@example.com|+15550101005|emma_agency|EMPLOYER|Run a boutique design agency. Frequently contracting creative talent.|https://linkedin.com/in/emma-wright"
  "frank.lopez@example.com|+15550101006|frank_dev|EMPLOYEE|Full-stack developer (React + Spring Boot). Open to freelance and full-time.|https://linkedin.com/in/frank-lopez"
  "grace.patel@example.com|+15550101007|grace_designs|EMPLOYEE|Product designer with 6 years in SaaS. Figma, prototyping, design systems.|https://linkedin.com/in/grace-patel"
  "henry.nguyen@example.com|+15550101008|henry_data|EMPLOYEE|Data analyst & Python automation. I turn messy spreadsheets into dashboards.|https://linkedin.com/in/henry-nguyen"
  "iris.johnson@example.com|+15550101009|iris_writes|EMPLOYEE|Freelance copywriter and content strategist. Blogs, landing pages, email.|https://linkedin.com/in/iris-johnson"
  "jack.brown@example.com|+15550101010|jack_mobile|EMPLOYEE|iOS & Android engineer. Available for contract app builds and audits.|https://linkedin.com/in/jack-brown"
)

# Build a JOB_REQUEST post body (employer hiring). Args: title company body-fields...
job_post() { # title|company|location|employmentType|category|salaryLower|salaryUpper|currency|frequency|negotiable|requirements|responsibilities|description
  IFS='|' read -r title company location etype category slo sup cur freq neg reqs resp desc <<<"$1"
  jq -nc \
    --arg title "$title" --arg company "$company" --arg location "$location" \
    --arg etype "$etype" --arg category "$category" \
    --argjson slo "$slo" --argjson sup "$sup" --arg cur "$cur" --arg freq "$freq" \
    --argjson neg "$neg" --arg reqs "$reqs" --arg resp "$resp" --arg desc "$desc" \
    '{
      title:$title, description:$desc,
      isCompany:true, companyName:$company,
      location:$location, employmentType:$etype, category:$category,
      salaryRangeLower:$slo, salaryRangeUpper:$sup,
      salaryCurrency:$cur, salaryFrequency:$freq, isNegotiable:$neg,
      requirements:$reqs, responsibilities:$resp
    }'
}

# Build a SERVICE_OFFER post body (employee offering a service). Args: |-fields
service_post() { # title|location|category|salary|currency|frequency|negotiable|deliveryDays|revisions|serviceIncludes|portfolio|description
  IFS='|' read -r title location category salary cur freq neg days revs includes portfolio desc <<<"$1"
  jq -nc \
    --arg title "$title" --arg location "$location" --arg category "$category" \
    --argjson salary "$salary" --arg cur "$cur" --arg freq "$freq" --argjson neg "$neg" \
    --argjson days "$days" --argjson revs "$revs" --arg includes "$includes" \
    --arg portfolio "$portfolio" --arg desc "$desc" \
    '{
      title:$title, description:$desc,
      isCompany:false,
      location:$location, employmentType:"FREELANCE", category:$category,
      salary:$salary, salaryCurrency:$cur, salaryFrequency:$freq, isNegotiable:$neg,
      toolkitExists:true, serviceDeliveryDays:$days, serviceRevisionCount:$revs,
      serviceIncludes:$includes, portfolioUrl:$portfolio
    }'
}

# ----------------------------------------------------------------------------
# Posts per user. Returned as newline-separated records; each record is a set
# of |-separated fields consumed by job_post() / service_post() above.
# (Functions + case are used instead of associative arrays so this runs on the
#  stock macOS bash 3.2, which has no `declare -A`.)
# ----------------------------------------------------------------------------
jobs_for() { # $1 = employer email -> JOB_REQUEST records
  case "$1" in
    alice.morgan@example.com) cat <<'EOF'
Senior Backend Engineer|Northwind Fintech|Remote|FULL_TIME|Software Development|90000|130000|USD|per year|true|5+ years Java/Spring Boot, PostgreSQL, REST API design.|Own core payment services, mentor juniors, drive architecture.|We are scaling our payments platform and need a senior backend engineer to lead service design.
React Frontend Developer|Northwind Fintech|Remote|FULL_TIME|Software Development|70000|100000|USD|per year|true|3+ years React, TypeScript, modern tooling.|Build customer-facing dashboards, collaborate with design.|Join our product team building the next generation of our customer dashboard.
EOF
      ;;
    ben.carter@example.com) cat <<'EOF'
Freelance Web Developer|Carter Studio|Hybrid - Austin, TX|CONTRACT|Software Development|40|70|USD|per hour|true|Portfolio of shipped web apps, comfortable with Next.js.|Deliver client websites end to end on tight timelines.|Short-term contract building marketing sites for our startup clients.
QA Tester (Part-time)|Carter Studio|Remote|PART_TIME|Quality Assurance|25|35|USD|per hour|false|Detail-oriented, experience with manual and automated testing.|Write test plans, file bugs, verify fixes.|We need a careful QA tester to keep our releases clean.
EOF
      ;;
    carla.diaz@example.com) cat <<'EOF'
Logistics Coordinator|SwiftMove Logistics|On-site - Chicago, IL|FULL_TIME|Operations|45000|55000|USD|per year|false|2+ years in logistics or supply chain, strong Excel.|Coordinate shipments, manage carrier relationships.|Fast-paced role coordinating daily shipments across the Midwest.
Customer Support Specialist|SwiftMove Logistics|Remote|FULL_TIME|Customer Support|40000|48000|USD|per year|true|Excellent communication, prior support experience.|Handle customer inquiries, resolve delivery issues.|Help our customers track and resolve shipping issues with empathy and speed.
EOF
      ;;
    david.kim@example.com) cat <<'EOF'
Android Engineer|TalentBridge (client)|Remote|FULL_TIME|Mobile Development|85000|120000|USD|per year|true|Kotlin, Jetpack Compose, 4+ years Android.|Build and maintain a consumer Android app with millions of users.|Hiring on behalf of a well-funded consumer app client.
DevOps Engineer|TalentBridge (client)|Remote|CONTRACT|DevOps|60|95|USD|per hour|true|AWS, Terraform, CI/CD pipelines, Kubernetes.|Own cloud infrastructure and deployment automation.|6-month contract with strong likelihood of extension.
EOF
      ;;
    emma.wright@example.com) cat <<'EOF'
Brand Designer|Wright Creative|Remote|CONTRACT|Design|50|85|USD|per hour|true|Strong branding portfolio, Adobe CC, Figma.|Develop brand identities for our agency clients.|Project-based brand design work for exciting consumer brands.
Motion Graphics Artist|Wright Creative|Remote|FREELANCE|Design|500|2000|USD|total|true|After Effects, storytelling, reel required.|Produce short promotional animations.|One-off freelance gig producing a 30-second promo animation.
EOF
      ;;
  esac
}

services_for() { # $1 = employee email -> SERVICE_OFFER records
  case "$1" in
    frank.lopez@example.com) cat <<'EOF'
Full-stack Web App Development|Remote|Software Development|1200|USD|total|true|14|3|Source code, deployment, 2 weeks support|https://franklopez.dev|I build production-ready web apps with React and Spring Boot, from idea to deployment.
Bug Fixing & Code Review|Remote|Software Development|60|USD|per hour|true|3|2|Detailed report, fixes, follow-up call|https://franklopez.dev|Stuck on a tricky bug? I will diagnose and fix it, then explain what went wrong.
EOF
      ;;
    grace.patel@example.com) cat <<'EOF'
SaaS Product UI/UX Design|Remote|Design|900|USD|total|true|10|3|Figma files, prototype, design tokens|https://gracepatel.design|End-to-end UI/UX for your SaaS product, including a clickable prototype.
Design System Setup|Remote|Design|1500|USD|total|false|21|2|Component library, documentation, handoff|https://gracepatel.design|I set up a scalable design system in Figma so your team ships consistent UI.
EOF
      ;;
    henry.nguyen@example.com) cat <<'EOF'
Data Dashboard Build|Remote|Data & Analytics|700|USD|total|true|7|2|Interactive dashboard, data pipeline, docs|https://henrynguyen.io|Turn your raw data into a clean, interactive dashboard you can actually use.
Python Automation Scripts|Remote|Data & Analytics|45|USD|per hour|true|4|3|Script, instructions, 1 week support|https://henrynguyen.io|I automate repetitive spreadsheet and reporting tasks with tidy Python scripts.
EOF
      ;;
    iris.johnson@example.com) cat <<'EOF'
Landing Page Copywriting|Remote|Writing & Content|350|USD|total|true|5|3|Headline, body copy, CTA variations|https://iriswrites.com|Conversion-focused landing page copy that turns visitors into customers.
Blog Content Package (4 posts)|Remote|Writing & Content|600|USD|total|true|14|2|4 SEO articles, meta descriptions, images sourced|https://iriswrites.com|A month of SEO-optimized blog content researched and written for your niche.
EOF
      ;;
    jack.brown@example.com) cat <<'EOF'
iOS App Development|Remote|Mobile Development|2500|USD|total|true|30|2|App Store submission, source code, support|https://jackbrown.app|I build polished native iOS apps and handle the full App Store submission.
Mobile App Audit|Remote|Mobile Development|400|USD|total|false|5|1|Performance report, recommendations, call|https://jackbrown.app|A thorough audit of your iOS or Android app covering performance and UX.
EOF
      ;;
  esac
}

# ----------------------------------------------------------------------------
# Main loop
# ----------------------------------------------------------------------------
created_users=0
created_posts=0

for entry in "${USERS[@]}"; do
  IFS='|' read -r email phone nick role about linkedin <<<"$entry"
  log "User: $email ($role)"

  register "$email" "$PASSWORD" "$phone"

  token=$(login "$email" "$PASSWORD")
  if [[ -z "$token" ]]; then
    err "could not authenticate $email — skipping their posts"
    continue
  fi
  ok "logged in"
  created_users=$((created_users + 1))

  update_profile "$token" "$nick" "$about" "$linkedin" "$role" >/dev/null
  set_preferred_role "$token" "$role" >/dev/null
  ok "profile + preferred role set"

  if [[ "$role" == "EMPLOYER" ]]; then
    endpoint="/api/v1/posts/create"
    raw="$(jobs_for "$email")"
    builder=job_post
  else
    endpoint="/api/v1/service-posts/create"
    raw="$(services_for "$email")"
    builder=service_post
  fi

  # Each user's posts are newline-separated; each line is |-separated fields.
  # NOTE: the backend's access token currently lives only ~10 seconds
  # (JwtService line ~112), so we re-authenticate right before each post to
  # guarantee a fresh token rather than reusing the login token above.
  while IFS= read -r line; do
    [[ -z "$line" ]] && continue
    body=$($builder "$line")
    fresh=$(login "$email" "$PASSWORD")
    create_post "${fresh:-$token}" "$endpoint" "$body"
    created_posts=$((created_posts + 1))
  done <<<"$raw"
done

echo
log "Done. Users processed: $created_users / ${#USERS[@]}.  Posts attempted: $created_posts."
log "Login for any account with password: $PASSWORD"
log "Example: $BASE_URL  ->  alice.morgan@example.com (employer) / frank.lopez@example.com (employee)"
