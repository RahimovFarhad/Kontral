import{c as a,j as r}from"./index-BZJxCf5k.js";import"./vendor-router-Mcnwvc-h.js";import{C as n}from"./chevron-right-CtxaMFVq.js";/**
 * @license lucide-react v0.539.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const d=[["path",{d:"M8 2v4",key:"1cmpym"}],["path",{d:"M16 2v4",key:"4m81vk"}],["rect",{width:"18",height:"18",x:"3",y:"4",rx:"2",key:"1hopcy"}],["path",{d:"M3 10h18",key:"8toen8"}]],y=a("calendar",d);/**
 * @license lucide-react v0.539.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const i=[["path",{d:"m15 18-6-6 6-6",key:"1wnfg3"}]],c=a("chevron-left",i),x=({currentPage:e,totalPages:o,onPageChange:t})=>r.jsxs("div",{className:"flex items-center justify-center space-x-2 mt-12",children:[r.jsx("button",{onClick:()=>t(Math.max(0,e-1)),disabled:e===0,className:"p-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50",children:r.jsx(c,{className:"h-4 w-4"})}),Array.from({length:Math.min(5,o)}).map((l,s)=>r.jsx("button",{onClick:()=>t(s),className:`px-3 py-2 rounded-lg transition-colors ${e===s?"bg-slate-900 text-white":"border border-gray-300 hover:bg-gray-50"}`,children:s+1},s)),o>5&&r.jsx("span",{className:"text-gray-500",children:"..."}),r.jsx("button",{onClick:()=>t(Math.min(o-1,e+1)),disabled:e===o-1,className:"p-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50",children:r.jsx(n,{className:"h-4 w-4"})})]});export{y as C,x as G,c as a};
