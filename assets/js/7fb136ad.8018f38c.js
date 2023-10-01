"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[631],{3905:(e,t,a)=>{a.d(t,{Zo:()=>m,kt:()=>d});var n=a(7294);function r(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function s(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,n)}return a}function l(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?s(Object(a),!0).forEach((function(t){r(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):s(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function i(e,t){if(null==e)return{};var a,n,r=function(e,t){if(null==e)return{};var a,n,r={},s=Object.keys(e);for(n=0;n<s.length;n++)a=s[n],t.indexOf(a)>=0||(r[a]=e[a]);return r}(e,t);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);for(n=0;n<s.length;n++)a=s[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(r[a]=e[a])}return r}var o=n.createContext({}),p=function(e){var t=n.useContext(o),a=t;return e&&(a="function"==typeof e?e(t):l(l({},t),e)),a},m=function(e){var t=p(e.components);return n.createElement(o.Provider,{value:t},e.children)},c="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},k=n.forwardRef((function(e,t){var a=e.components,r=e.mdxType,s=e.originalType,o=e.parentName,m=i(e,["components","mdxType","originalType","parentName"]),c=p(a),k=r,d=c["".concat(o,".").concat(k)]||c[k]||u[k]||s;return a?n.createElement(d,l(l({ref:t},m),{},{components:a})):n.createElement(d,l({ref:t},m))}));function d(e,t){var a=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var s=a.length,l=new Array(s);l[0]=k;var i={};for(var o in t)hasOwnProperty.call(t,o)&&(i[o]=t[o]);i.originalType=e,i[c]="string"==typeof e?e:r,l[1]=i;for(var p=2;p<s;p++)l[p]=a[p];return n.createElement.apply(null,l)}return n.createElement.apply(null,a)}k.displayName="MDXCreateElement"},2329:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>o,contentTitle:()=>l,default:()=>u,frontMatter:()=>s,metadata:()=>i,toc:()=>p});var n=a(7462),r=(a(7294),a(3905));const s={sidebar_position:1,id:"intro",title:"Just SemVer",slug:"/"},l="just-semver",i={unversionedId:"intro",id:"intro",title:"Just SemVer",description:"Build Status",source:"@site/../generated-docs/docs/intro.md",sourceDirName:".",slug:"/",permalink:"/docs/",draft:!1,tags:[],version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1,id:"intro",title:"Just SemVer",slug:"/"},sidebar:"tutorialSidebar",next:{title:"How to Use",permalink:"/docs/how-to-use/"}},o={},p=[{value:"Get just-semver",id:"get-just-semver",level:2},{value:"<code>0.12.0</code>",id:"0120",level:3},{value:"Older Versions",id:"older-versions",level:2},{value:"<code>0.11.0</code>",id:"0110",level:3},{value:"Get just-semver",id:"get-just-semver-1",level:4},{value:"<code>0.10.0</code>",id:"0100",level:3},{value:"Get just-semver",id:"get-just-semver-2",level:4},{value:"<code>0.9.0</code>",id:"090",level:3},{value:"Get just-semver",id:"get-just-semver-3",level:4},{value:"<code>0.6.0</code>",id:"060",level:3},{value:"Get just-semver",id:"get-just-semver-4",level:4}],m={toc:p},c="wrapper";function u(e){let{components:t,...a}=e;return(0,r.kt)(c,(0,n.Z)({},m,a,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"just-semver"},"just-semver"),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"https://github.com/Kevin-Lee/just-semver/actions?workflow=Build+All"},(0,r.kt)("img",{parentName:"a",src:"https://github.com/Kevin-Lee/just-semver/workflows/Build%20All/badge.svg",alt:"Build Status"})),"\n",(0,r.kt)("a",{parentName:"p",href:"https://github.com/Kevin-Lee/just-semver/actions?workflow=Release"},(0,r.kt)("img",{parentName:"a",src:"https://github.com/Kevin-Lee/just-semver/workflows/Release/badge.svg",alt:"Release Status"})),"\n",(0,r.kt)("a",{parentName:"p",href:"https://coveralls.io/github/Kevin-Lee/just-semver?branch=master"},(0,r.kt)("img",{parentName:"a",src:"https://coveralls.io/repos/github/Kevin-Lee/just-semver/badge.svg?branch=master",alt:"Coverage Status"}))),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"https://search.maven.org/artifact/io.kevinlee/just-semver_2.13"},(0,r.kt)("img",{parentName:"a",src:"https://maven-badges.herokuapp.com/maven-central/io.kevinlee/just-semver_2.13/badge.svg",alt:"Maven Central"})),"\n",(0,r.kt)("a",{parentName:"p",href:"https://index.scala-lang.org/kevin-lee/just-semver/just-semver"},(0,r.kt)("img",{parentName:"a",src:"https://index.scala-lang.org/kevin-lee/just-semver/just-semver/latest.svg",alt:"Latest version"}))),(0,r.kt)("p",null,"Semantic Versioning (",(0,r.kt)("inlineCode",{parentName:"p"},"SemVer"),") for Scala"),(0,r.kt)("admonition",{type:"info"},(0,r.kt)("p",{parentName:"admonition"},"Supported Scala Versions: ",(0,r.kt)("inlineCode",{parentName:"p"},"2.12"),", ",(0,r.kt)("inlineCode",{parentName:"p"},"2.13")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"3.3+")),(0,r.kt)("p",{parentName:"admonition"},"Show ",(0,r.kt)("a",{parentName:"p",href:"https://index.scala-lang.org/kevin-lee/just-semver/artifacts"},(0,r.kt)("strong",{parentName:"a"},"all ",(0,r.kt)("inlineCode",{parentName:"strong"},"just-semver")," versions")))),(0,r.kt)("h2",{id:"get-just-semver"},"Get just-semver"),(0,r.kt)("h3",{id:"0120"},(0,r.kt)("inlineCode",{parentName:"h3"},"0.12.0")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %% "just-semver-core" % "0.12.0"\n')),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"https://www.scala-js.org"},(0,r.kt)("img",{parentName:"a",src:"https://www.scala-js.org/assets/badges/scalajs-1.11.0.svg",alt:"Scala.js"}))),(0,r.kt)("p",null,"Since ",(0,r.kt)("inlineCode",{parentName:"p"},"0.6.0")," ",(0,r.kt)("inlineCode",{parentName:"p"},"just-semver")," supports Scala.js."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %%% "just-semver-core" % "0.12.0"\n')),(0,r.kt)("p",null,"e.g.)"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %% "just-semver-core" % "0.12.0"\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %%% "just-semver-core" % "0.12.0"\n')),(0,r.kt)("h2",{id:"older-versions"},"Older Versions"),(0,r.kt)("h3",{id:"0110"},(0,r.kt)("inlineCode",{parentName:"h3"},"0.11.0")),(0,r.kt)("admonition",{type:"info"},(0,r.kt)("p",{parentName:"admonition"},"Supported Scala Versions: ",(0,r.kt)("inlineCode",{parentName:"p"},"2.12"),", ",(0,r.kt)("inlineCode",{parentName:"p"},"2.13")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"3.3+"),"."),(0,r.kt)("p",{parentName:"admonition"},"Show ",(0,r.kt)("a",{parentName:"p",href:"https://index.scala-lang.org/kevin-lee/just-semver/artifacts"},(0,r.kt)("strong",{parentName:"a"},"all ",(0,r.kt)("inlineCode",{parentName:"strong"},"just-semver")," versions")))),(0,r.kt)("h4",{id:"get-just-semver-1"},"Get just-semver"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %% "just-semver-core" % "0.11.0"\n')),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"https://www.scala-js.org"},(0,r.kt)("img",{parentName:"a",src:"https://www.scala-js.org/assets/badges/scalajs-1.11.0.svg",alt:"Scala.js"}))),(0,r.kt)("p",null,"Since ",(0,r.kt)("inlineCode",{parentName:"p"},"0.6.0")," ",(0,r.kt)("inlineCode",{parentName:"p"},"just-semver")," supports Scala.js."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %%% "just-semver-core" % "0.11.0"\n')),(0,r.kt)("p",null,"e.g.)"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %% "just-semver-core" % "0.11.0"\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %%% "just-semver-core" % "0.11.0"\n')),(0,r.kt)("h3",{id:"0100"},(0,r.kt)("inlineCode",{parentName:"h3"},"0.10.0")),(0,r.kt)("admonition",{type:"info"},(0,r.kt)("p",{parentName:"admonition"},"Supported Scala Versions: ",(0,r.kt)("inlineCode",{parentName:"p"},"2.12"),", ",(0,r.kt)("inlineCode",{parentName:"p"},"2.13")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"3.2+"),"."),(0,r.kt)("p",{parentName:"admonition"},"Show ",(0,r.kt)("a",{parentName:"p",href:"https://index.scala-lang.org/kevin-lee/just-semver/artifacts"},(0,r.kt)("strong",{parentName:"a"},"all ",(0,r.kt)("inlineCode",{parentName:"strong"},"just-semver")," versions")))),(0,r.kt)("h4",{id:"get-just-semver-2"},"Get just-semver"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %% "just-semver-core" % "0.10.0"\n')),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"https://www.scala-js.org"},(0,r.kt)("img",{parentName:"a",src:"https://www.scala-js.org/assets/badges/scalajs-1.11.0.svg",alt:"Scala.js"}))),(0,r.kt)("p",null,"Since ",(0,r.kt)("inlineCode",{parentName:"p"},"0.6.0")," ",(0,r.kt)("inlineCode",{parentName:"p"},"just-semver")," supports Scala.js."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %%% "just-semver-core" % "0.10.0"\n')),(0,r.kt)("p",null,"e.g.)"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %% "just-semver-core" % "0.10.0"\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %%% "just-semver-core" % "0.10.0"\n')),(0,r.kt)("h3",{id:"090"},(0,r.kt)("inlineCode",{parentName:"h3"},"0.9.0")),(0,r.kt)("admonition",{type:"info"},(0,r.kt)("p",{parentName:"admonition"},"Supported Scala Versions: ",(0,r.kt)("inlineCode",{parentName:"p"},"2.12"),", ",(0,r.kt)("inlineCode",{parentName:"p"},"2.13")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"3.1+"),"."),(0,r.kt)("p",{parentName:"admonition"},"Show ",(0,r.kt)("a",{parentName:"p",href:"https://index.scala-lang.org/kevin-lee/just-semver/artifacts"},(0,r.kt)("strong",{parentName:"a"},"all ",(0,r.kt)("inlineCode",{parentName:"strong"},"just-semver")," versions")))),(0,r.kt)("h4",{id:"get-just-semver-3"},"Get just-semver"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %% "just-semver-core" % "0.9.0"\n')),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"https://www.scala-js.org"},(0,r.kt)("img",{parentName:"a",src:"https://www.scala-js.org/assets/badges/scalajs-1.11.0.svg",alt:"Scala.js"}))),(0,r.kt)("p",null,"Since ",(0,r.kt)("inlineCode",{parentName:"p"},"0.6.0")," ",(0,r.kt)("inlineCode",{parentName:"p"},"just-semver")," supports Scala.js."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %%% "just-semver-core" % "0.9.0"\n')),(0,r.kt)("p",null,"e.g.)"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %% "just-semver-core" % "0.9.0"\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %%% "just-semver-core" % "0.9.0"\n')),(0,r.kt)("h3",{id:"060"},(0,r.kt)("inlineCode",{parentName:"h3"},"0.6.0")),(0,r.kt)("admonition",{type:"info"},(0,r.kt)("p",{parentName:"admonition"},"Supported Scala Versions: ",(0,r.kt)("inlineCode",{parentName:"p"},"2.11"),", ",(0,r.kt)("inlineCode",{parentName:"p"},"2.12"),", ",(0,r.kt)("inlineCode",{parentName:"p"},"2.13")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"3"),"."),(0,r.kt)("p",{parentName:"admonition"},"Show ",(0,r.kt)("a",{parentName:"p",href:"https://index.scala-lang.org/kevin-lee/just-semver/artifacts"},(0,r.kt)("strong",{parentName:"a"},"all ",(0,r.kt)("inlineCode",{parentName:"strong"},"just-semver")," versions")))),(0,r.kt)("h4",{id:"get-just-semver-4"},"Get just-semver"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %% "just-semver-core" % "0.6.0"\n')),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"https://www.scala-js.org"},(0,r.kt)("img",{parentName:"a",src:"https://www.scala-js.org/assets/badges/scalajs-1.11.0.svg",alt:"Scala.js"}))),(0,r.kt)("p",null,"Since ",(0,r.kt)("inlineCode",{parentName:"p"},"0.6.0")," ",(0,r.kt)("inlineCode",{parentName:"p"},"just-semver")," supports Scala.js."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %%% "just-semver-core" % "0.6.0"\n')),(0,r.kt)("p",null,"e.g.)"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %% "just-semver-core" % "0.6.0"\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %%% "just-semver-core" % "0.6.0"\n')))}u.isMDXComponent=!0}}]);