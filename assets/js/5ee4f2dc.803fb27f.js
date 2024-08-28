"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[645],{920:(e,r,n)=>{n.r(r),n.d(r,{assets:()=>o,contentTitle:()=>l,default:()=>c,frontMatter:()=>t,metadata:()=>i,toc:()=>u});var a=n(4848),s=n(8453);const t={},l="SemVer (Semantic Version)",i={id:"semver/semver",title:"SemVer (Semantic Version)",description:"For now, please do not use any types and methods from the package other than just.semver.",source:"@site/../generated-docs/docs/semver/semver.md",sourceDirName:"semver",slug:"/semver/",permalink:"/docs/semver/",draft:!1,unlisted:!1,tags:[],version:"current",frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Just SemVer",permalink:"/docs/"},next:{title:"DecVer",permalink:"/docs/decver/"}},o={},u=[{value:"<code>SemVer.parse</code>",id:"semverparse",level:2},{value:"<code>SemVer.unsafeParse</code>",id:"semverunsafeparse",level:2},{value:"SemVer with <code>pre-release</code> info",id:"semver-with-pre-release-info",level:2},{value:"SemVer with build <code>meta-info</code>",id:"semver-with-build-meta-info",level:2},{value:"SemVer with <code>pre-release</code> info and build <code>meta-info</code>",id:"semver-with-pre-release-info-and-build-meta-info",level:2},{value:"Compare <code>SemVer</code>",id:"compare-semver",level:2},{value:"Matchers",id:"matchers",level:2}];function m(e){const r={admonition:"admonition",code:"code",h1:"h1",h2:"h2",header:"header",hr:"hr",li:"li",p:"p",pre:"pre",ul:"ul",...(0,s.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(r.header,{children:(0,a.jsx)(r.h1,{id:"semver-semantic-version",children:"SemVer (Semantic Version)"})}),"\n",(0,a.jsxs)(r.admonition,{title:"NOTE",type:"caution",children:[(0,a.jsxs)(r.p,{children:["For now, please do not use any types and methods from the package other than ",(0,a.jsx)(r.code,{children:"just.semver"}),"."]}),(0,a.jsxs)(r.ul,{children:["\n",(0,a.jsxs)(r.li,{children:[(0,a.jsx)(r.code,{children:"just.semver"}),": Fine"]}),"\n",(0,a.jsxs)(r.li,{children:[(0,a.jsx)(r.code,{children:"just.semver.matcher"})," or any other ",(0,a.jsx)(r.code,{children:"just.semver.xxx"})," packages: You can use it but not recommended as it's currently experimental."]}),"\n"]})]}),"\n",(0,a.jsx)(r.hr,{}),"\n",(0,a.jsxs)(r.p,{children:["It requires the ",(0,a.jsx)(r.code,{children:"just-semver-core"})," module."]}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-scala",children:'"io.kevinlee" %% "just-semver-core" % "1.0.0"\n'})}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-scala",children:'"io.kevinlee" %%% "just-semver-core" % "1.0.0"\n'})}),"\n",(0,a.jsx)(r.h2,{id:"semverparse",children:(0,a.jsx)(r.code,{children:"SemVer.parse"})}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-scala",children:'import just.semver.SemVer\n\nval v = SemVer.parse("1.0.0")\n// v: Either[just.semver.ParseError, SemVer] = Right(\n//   value = SemVer(\n//     major = Major(value = 1),\n//     minor = Minor(value = 0),\n//     patch = Patch(value = 0),\n//     pre = None,\n//     buildMetadata = None\n//   )\n// )\n\n// To render it to `String`,\nv.map(_.render)\n// res1: Either[just.semver.ParseError, String] = Right(value = "1.0.0")\n\n// Invalid version\nSemVer.parse("a1.0.0")\n// res2: Either[just.semver.ParseError, SemVer] = Left(\n//   value = InvalidVersionStringError(value = "a1.0.0")\n// )\n'})}),"\n",(0,a.jsx)(r.h2,{id:"semverunsafeparse",children:(0,a.jsx)(r.code,{children:"SemVer.unsafeParse"})}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-scala",children:'import just.semver.SemVer\n\n// parse unsafe - NOT RECOMMENDED!!!\nval v = SemVer.unsafeParse("1.0.0")\n// v: SemVer = SemVer(\n//   major = Major(value = 1),\n//   minor = Minor(value = 0),\n//   patch = Patch(value = 0),\n//   pre = None,\n//   buildMetadata = None\n// )\n\n// to String\nv.render\n// res4: String = "1.0.0"\n'})}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-scala",children:'\n// Invalid version\nSemVer.unsafeParse("a1.0.0")\n// java.lang.RuntimeException: Invalid SemVer String. value: a1.0.0\n// \tat scala.sys.package$.error(package.scala:27)\n// \tat just.semver.SemVer$.unsafeParse(SemVer.scala:127)\n// \tat repl.MdocSession$MdocApp3$$anonfun$2.apply(semver.md:42)\n// \tat repl.MdocSession$MdocApp3$$anonfun$2.apply(semver.md:42)\n'})}),"\n",(0,a.jsxs)(r.h2,{id:"semver-with-pre-release-info",children:["SemVer with ",(0,a.jsx)(r.code,{children:"pre-release"})," info"]}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-scala",children:'import just.semver.SemVer\n\nSemVer.parse("1.0.0-beta1")\n// res6: Either[just.semver.ParseError, SemVer] = Right(\n//   value = SemVer(\n//     major = Major(value = 1),\n//     minor = Minor(value = 0),\n//     patch = Patch(value = 0),\n//     pre = Some(\n//       value = PreRelease(\n//         identifier = List(\n//           Dsv(values = List(Alphabet(value = "beta"), Num(value = "1")))\n//         )\n//       )\n//     ),\n//     buildMetadata = None\n//   )\n// )\n\nval v = SemVer.parse("1.0.0-3.123.9a")\n// v: Either[just.semver.ParseError, SemVer] = Right(\n//   value = SemVer(\n//     major = Major(value = 1),\n//     minor = Minor(value = 0),\n//     patch = Patch(value = 0),\n//     pre = Some(\n//       value = PreRelease(\n//         identifier = List(\n//           Dsv(values = List(Num(value = "3"))),\n//           Dsv(values = List(Num(value = "123"))),\n//           Dsv(values = List(Num(value = "9"), Alphabet(value = "a")))\n//         )\n//       )\n//     ),\n//     buildMetadata = None\n//   )\n// )\n\nv.map(_.render)\n// res7: Either[just.semver.ParseError, String] = Right(\n//   value = "1.0.0-3.123.9a"\n// )\n'})}),"\n",(0,a.jsxs)(r.h2,{id:"semver-with-build-meta-info",children:["SemVer with build ",(0,a.jsx)(r.code,{children:"meta-info"})]}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-scala",children:'import just.semver.SemVer\n\nval v = SemVer.parse("1.0.0+100.0.12abc")\n// v: Either[just.semver.ParseError, SemVer] = Right(\n//   value = SemVer(\n//     major = Major(value = 1),\n//     minor = Minor(value = 0),\n//     patch = Patch(value = 0),\n//     pre = None,\n//     buildMetadata = Some(\n//       value = BuildMetaInfo(\n//         identifier = List(\n//           Dsv(values = List(Num(value = "100"))),\n//           Dsv(values = List(Num(value = "0"))),\n//           Dsv(values = List(Num(value = "12"), Alphabet(value = "abc")))\n//         )\n//       )\n//     )\n//   )\n// )\n\nv.map(_.render)\n// res9: Either[just.semver.ParseError, String] = Right(\n//   value = "1.0.0+100.0.12abc"\n// )\n'})}),"\n",(0,a.jsxs)(r.h2,{id:"semver-with-pre-release-info-and-build-meta-info",children:["SemVer with ",(0,a.jsx)(r.code,{children:"pre-release"})," info and build ",(0,a.jsx)(r.code,{children:"meta-info"})]}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-scala",children:'import just.semver.SemVer\n\nSemVer.parse("1.0.0-beta1")\n// res11: Either[just.semver.ParseError, SemVer] = Right(\n//   value = SemVer(\n//     major = Major(value = 1),\n//     minor = Minor(value = 0),\n//     patch = Patch(value = 0),\n//     pre = Some(\n//       value = PreRelease(\n//         identifier = List(\n//           Dsv(values = List(Alphabet(value = "beta"), Num(value = "1")))\n//         )\n//       )\n//     ),\n//     buildMetadata = None\n//   )\n// )\n\nval v = SemVer.parse("1.0.0-3.123.9a+100.0.12abc")\n// v: Either[just.semver.ParseError, SemVer] = Right(\n//   value = SemVer(\n//     major = Major(value = 1),\n//     minor = Minor(value = 0),\n//     patch = Patch(value = 0),\n//     pre = Some(\n//       value = PreRelease(\n//         identifier = List(\n//           Dsv(values = List(Num(value = "3"))),\n//           Dsv(values = List(Num(value = "123"))),\n//           Dsv(values = List(Num(value = "9"), Alphabet(value = "a")))\n//         )\n//       )\n//     ),\n//     buildMetadata = Some(\n//       value = BuildMetaInfo(\n//         identifier = List(\n//           Dsv(values = List(Num(value = "100"))),\n//           Dsv(values = List(Num(value = "0"))),\n//           Dsv(values = List(Num(value = "12"), Alphabet(value = "abc")))\n//         )\n//       )\n//     )\n//   )\n// )\n\nv.map(_.render)\n// res12: Either[just.semver.ParseError, String] = Right(\n//   value = "1.0.0-3.123.9a+100.0.12abc"\n// )\n'})}),"\n",(0,a.jsxs)(r.h2,{id:"compare-semver",children:["Compare ",(0,a.jsx)(r.code,{children:"SemVer"})]}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-scala",children:'import just.semver.SemVer\n\nfor {\n a <- SemVer.parse("1.0.0")\n b <- SemVer.parse("1.0.1")\n} yield a < b\n// res14: Either[just.semver.ParseError, Boolean] = Right(value = true)\n\nfor {\n a <- SemVer.parse("1.0.1")\n b <- SemVer.parse("1.0.0")\n} yield a < b\n// res15: Either[just.semver.ParseError, Boolean] = Right(value = false)\n\nfor {\n a <- SemVer.parse("1.0.0")\n b <- SemVer.parse("1.0.1")\n} yield a <= b\n// res16: Either[just.semver.ParseError, Boolean] = Right(value = true)\n\nfor {\n a <- SemVer.parse("1.0.0")\n b <- SemVer.parse("1.0.0")\n} yield a <= b\n// res17: Either[just.semver.ParseError, Boolean] = Right(value = true)\n\nfor {\n a <- SemVer.parse("1.0.0")\n b <- SemVer.parse("1.0.0")\n} yield a == b\n// res18: Either[just.semver.ParseError, Boolean] = Right(value = true)\n\nfor {\n a <- SemVer.parse("1.0.1")\n b <- SemVer.parse("1.0.0")\n} yield a > b\n// res19: Either[just.semver.ParseError, Boolean] = Right(value = true)\n\nfor {\n a <- SemVer.parse("1.0.0")\n b <- SemVer.parse("1.0.1")\n} yield a > b\n// res20: Either[just.semver.ParseError, Boolean] = Right(value = false)\n\nfor {\n a <- SemVer.parse("1.0.0")\n b <- SemVer.parse("1.0.1")\n} yield a >= b\n// res21: Either[just.semver.ParseError, Boolean] = Right(value = false)\n\nfor {\n a <- SemVer.parse("1.0.0")\n b <- SemVer.parse("1.0.0")\n} yield a >= b\n// res22: Either[just.semver.ParseError, Boolean] = Right(value = true)\n\nfor {\n a <- SemVer.parse("1.0.1")\n b <- SemVer.parse("1.0.0")\n} yield a >= b\n// res23: Either[just.semver.ParseError, Boolean] = Right(value = true)\n'})}),"\n",(0,a.jsx)(r.h2,{id:"matchers",children:"Matchers"}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-scala",children:'SemVer.unsafeParse("1.0.0").unsafeMatches("1.0.0 - 2.0.0")\n// res24: Boolean = true\nSemVer.unsafeParse("1.5.0").unsafeMatches("1.0.0 - 2.0.0")\n// res25: Boolean = true\nSemVer.unsafeParse("2.0.0").unsafeMatches("1.0.0 - 2.0.0")\n// res26: Boolean = true\nSemVer.unsafeParse("0.9.9").unsafeMatches("1.0.0 - 2.0.0")\n// res27: Boolean = false\nSemVer.unsafeParse("2.0.1").unsafeMatches("1.0.0 - 2.0.0")\n// res28: Boolean = false\n\nSemVer.unsafeParse("1.0.0").unsafeMatches(">1.0.0 <2.0.0")\n// res29: Boolean = false\nSemVer.unsafeParse("1.0.0").unsafeMatches(">=1.0.0 <=2.0.0")\n// res30: Boolean = true\nSemVer.unsafeParse("1.5.0").unsafeMatches(">1.0.0 <2.0.0")\n// res31: Boolean = true\nSemVer.unsafeParse("2.0.0").unsafeMatches(">1.0.0 <2.0.0")\n// res32: Boolean = false\nSemVer.unsafeParse("2.0.0").unsafeMatches(">=1.0.0 <=2.0.0")\n// res33: Boolean = true\nSemVer.unsafeParse("0.9.9").unsafeMatches(">=1.0.0 <=2.0.0")\n// res34: Boolean = false\nSemVer.unsafeParse("2.0.1").unsafeMatches(">=1.0.0 <=2.0.0")\n// res35: Boolean = false\n\nSemVer.unsafeParse("1.0.0").unsafeMatches("1.0.0 - 2.0.0 || >3.0.0 <4.0.0")\n// res36: Boolean = true\nSemVer.unsafeParse("2.0.0").unsafeMatches("1.0.0 - 2.0.0 || >3.0.0 <4.0.0")\n// res37: Boolean = true\nSemVer.unsafeParse("3.0.0").unsafeMatches("1.0.0 - 2.0.0 || >3.0.0 <=4.0.0")\n// res38: Boolean = false\nSemVer.unsafeParse("3.0.1").unsafeMatches("1.0.0 - 2.0.0 || >3.0.0 <=4.0.0")\n// res39: Boolean = true\nSemVer.unsafeParse("4.0.0").unsafeMatches("1.0.0 - 2.0.0 || >3.0.0 <=4.0.0")\n// res40: Boolean = true\n'})})]})}function c(e={}){const{wrapper:r}={...(0,s.R)(),...e.components};return r?(0,a.jsx)(r,{...e,children:(0,a.jsx)(m,{...e})}):m(e)}},8453:(e,r,n)=>{n.d(r,{R:()=>l,x:()=>i});var a=n(6540);const s={},t=a.createContext(s);function l(e){const r=a.useContext(t);return a.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function i(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:l(e.components),a.createElement(t.Provider,{value:r},e.children)}}}]);