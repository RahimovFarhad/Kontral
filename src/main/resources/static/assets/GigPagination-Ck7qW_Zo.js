import{c as a,j as o}from"./index-j7QH_n4u.js";import"./vendor-router-C3kIuND3.js";import{C as c}from"./chevron-right-BRunBOJp.js";/**
 * @license lucide-react v0.539.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const n=[["path",{d:"M8 2v4",key:"1cmpym"}],["path",{d:"M16 2v4",key:"4m81vk"}],["rect",{width:"18",height:"18",x:"3",y:"4",rx:"2",key:"1hopcy"}],["path",{d:"M3 10h18",key:"8toen8"}]],x=a("calendar",n);/**
 * @license lucide-react v0.539.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const d=[["path",{d:"m15 18-6-6 6-6",key:"1wnfg3"}]],i=a("chevron-left",d);/**
 * @license lucide-react v0.539.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const l=[["path",{d:"M12 6v6l4 2",key:"mmk7yg"}],["circle",{cx:"12",cy:"12",r:"10",key:"1mglay"}]],b=a("clock",l),k=({currentPage:e,totalPages:r,onPageChange:t})=>o.jsxs("div",{className:"flex items-center justify-center space-x-2 mt-12",children:[o.jsx("button",{onClick:()=>t(Math.max(0,e-1)),disabled:e===0,className:"p-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50",children:o.jsx(i,{className:"h-4 w-4"})}),Array.from({length:Math.min(5,r)}).map((h,s)=>o.jsx("button",{onClick:()=>t(s),className:`px-3 py-2 rounded-lg transition-colors ${e===s?"bg-slate-900 text-white":"border border-gray-300 hover:bg-gray-50"}`,children:s+1},s)),r>5&&o.jsx("span",{className:"text-gray-500",children:"..."}),o.jsx("button",{onClick:()=>t(Math.min(r-1,e+1)),disabled:e===r-1,className:"p-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50",children:o.jsx(c,{className:"h-4 w-4"})})]});export{b as C,k as G,x as a,i as b};
