
RGraph=window.RGraph||{isRGraph:true};RGraph.Radar=function(conf)
{if(typeof conf==='object'&&typeof conf.data==='object'&&typeof conf.id==='string'){var parseConfObjectForOptions=true;if(typeof conf.data[0]==='number'||typeof conf.data[0]==='string'){conf.data=[conf.data];}}else{var conf={id:conf,data:[]};if(typeof arguments[1]==='object'&&typeof arguments[1][0]==='number'){for(var i=1;i<arguments.length;++i){conf.data.push(RGraph.arrayClone(arguments[i]));}}else if(typeof arguments[1]==='object'&&typeof arguments[1][0]==='object'&&typeof arguments[1][0][0]==='number'){conf.data=RGraph.arrayClone(arguments[1]);}}
this.id=conf.id;this.canvas=document.getElementById(conf.id);this.context=this.canvas.getContext?this.canvas.getContext("2d"):null;this.canvas.__object__=this;this.type='radar';this.isRGraph=true;this.data=[];this.max=0;this.uid=RGraph.CreateUID();this.canvas.uid=this.canvas.uid?this.canvas.uid:RGraph.CreateUID();this.colorsParsed=false;this.coords=[];this.coordsText=[];this.original_data=[];this.original_colors=[];this.firstDraw=true;this.propertyNameAliases={};for(var i=0,len=conf.data.length;i<len;++i){for(var j=0;j<conf.data[i].length;++j){if(typeof conf.data[i][j]==='string'){conf.data[i][j]=parseFloat(conf.data[i][j]);}}
this.original_data.push(RGraph.arrayClone(conf.data[i]));this.data.push(RGraph.arrayClone(conf.data[i]));this.max=Math.max(this.max,RGraph.arrayMax(conf.data[i]));}
this.properties={'chart.margin.left':25,'chart.margin.right':25,'chart.margin.top':25,'chart.margin.bottom':25,'chart.linewidth':1,'chart.colors.stroke':'#aaa','chart.colors':['rgba(255,0,0,0.75)','rgba(0,255,255,0.25)','rgba(255,0,0,0.5)','red','green','blue','pink','aqua','brown','orange','grey'],'chart.colors.alpha':null,'chart.circle':0,'chart.circle.fill':'red','chart.circle.stroke':'black','chart.labels':[],'chart.labels.font':null,'chart.labels.size':null,'chart.labels.color':null,'chart.labels.bold':null,'chart.labels.size':null,'chart.labels.offset':10,'chart.labels.axes':'','chart.labels.background.fill':'white','chart.labels.boxed':false,'chart.labels.axes.font':null,'chart.labels.axes.size':null,'chart.labels.axes.color':null,'chart.labels.axes.bold':null,'chart.labels.axes.italic':null,'chart.labels.axes.boxed':null,'chart.labels.axes.boxed.zero':true,'chart.labels.axes.boxed.background':'rgba(255,255,255,0.7)','chart.labels.axes.specific':null,'chart.labels.axes.count':5,'chart.background.circles':true,'chart.background.circles.count':null,'chart.background.circles.color':'#ddd','chart.background.circles.poly':true,'chart.background.circles.spokes':24,'chart.text.size':12,'chart.text.font':'Arial, Verdana, sans-serif','chart.text.color':'black','chart.text.bold':false,'chart.text.italic':false,'chart.text.accessible':true,'chart.text.accessible.overflow':'visible','chart.text.accessible.pointerevents':false,'chart.title':'','chart.title.background':null,'chart.title.hpos':null,'chart.title.vpos':null,'chart.title.color':null,'chart.title.bold':null,'chart.title.italic':null,'chart.title.size':null,'chart.title.font':null,'chart.title.x':null,'chart.title.y':null,'chart.title.halign':null,'chart.title.valign':null,'chart.linewidth':1,'chart.key':null,'chart.key.background':'white','chart.key.shadow':false,'chart.key.shadow.color':'#666','chart.key.shadow.blur':3,'chart.key.shadow.offsetx':2,'chart.key.shadow.offsety':2,'chart.key.position':'graph','chart.key.halign':'right','chart.key.position.gutter.boxed':false,'chart.key.position.x':null,'chart.key.position.y':null,'chart.key.color.shape':'square','chart.key.rounded':true,'chart.key.linewidth':1,'chart.key.colors':null,'chart.key.interactive':false,'chart.key.interactive.highlight.chart.stroke':'rgba(255,0,0,0.3)','chart.key.interactive.highlight.label':'rgba(255,0,0,0.2)','chart.key.labels.color':null,'chart.key.labels.font':null,'chart.key.labels.size':null,'chart.key.labels.bold':null,'chart.key.labels.italic':null,'chart.key.labels.offsetx':0,'chart.key.labels.offsety':0,'chart.contextmenu':null,'chart.annotatable':false,'chart.annotate.color':'black','chart.annotate.linewidth':1,'chart.tooltips.effect':'fade','chart.tooltips.event':'onmousemove','chart.tooltips.css.class':'RGraph_tooltip','chart.tooltips.highlight':true,'chart.highlight.stroke':'gray','chart.highlight.fill':'rgba(255,255,255,0.7)','chart.highlight.point.radius':2,'chart.resizable':false,'chart.resize.handle.adjust':[0,0],'chart.resize.handle.background':null,'chart.scale.max':null,'chart.scale.decimals':0,'chart.scale.point':'.','chart.scale.thousand':',','chart.scale.units.pre':'','chart.scale.units.post':'','chart.accumulative':false,'chart.radius':null,'chart.events.click':null,'chart.events.mousemove':null,'chart.tooltips':null,'chart.tooltips.event':'onmousemove','chart.centerx':null,'chart.centery':null,'chart.radius':null,'chart.xaxis.tickmarks.count':5,'chart.yaxis.tickmarks.count':5,'chart.axes.color':'rgba(0,0,0,0)','chart.highlights':false,'chart.highlights.stroke':'#ddd','chart.highlights.fill':null,'chart.highlights.radius':3,'chart.fill.click':null,'chart.fill.mousemove':null,'chart.fill.tooltips':null,'chart.fill.highlight.fill':'rgba(255,255,255,0.7)','chart.fill.highlight.stroke':'rgba(0,0,0,0)','chart.fill.mousemove.redraw':false,'chart.animation.trace.clip':1,'chart.clearto':'rgba(0,0,0,0)'}
for(var dataset=0;dataset<this.data.length;++dataset){if(this.data[dataset].length<3){alert('[RADAR] You must specify at least 3 data points');return;}}
var idx=0;for(var dataset=0;dataset<this.data.length;++dataset){for(var i=0,len=this.data[dataset].length;i<len;++i){this['$'+(idx++)]={};}}
if(!this.canvas.__rgraph_aa_translated__){this.context.translate(0.5,0.5);this.canvas.__rgraph_aa_translated__=true;}
var RG=RGraph,ca=this.canvas,co=ca.getContext('2d'),prop=this.properties,pa2=RG.path2,win=window,doc=document,ma=Math
if(RG.Effects&&typeof RG.Effects.decorate==='function'){RG.Effects.decorate(this);}
this.set=this.Set=function(name,value)
{var value=typeof arguments[1]==='undefined'?null:arguments[1];if(arguments.length===1&&typeof name==='object'){RG.parseObjectStyleConfig(this,name);return this;}
if(name.substr(0,6)!='chart.'){name='chart.'+name;}
while(name.match(/([A-Z])/)){name=name.replace(/([A-Z])/,'.'+RegExp.$1.toLowerCase());}
prop[name]=value;return this;};this.get=this.Get=function(name)
{if(name.substr(0,6)!='chart.'){name='chart.'+name;}
while(name.match(/([A-Z])/)){name=name.replace(/([A-Z])/,'.'+RegExp.$1.toLowerCase());}
return prop[name];};this.draw=this.Draw=function()
{RG.FireCustomEvent(this,'onbeforedraw');this.coords=[];this.coords2=[];this.coordsText=[];this.data=RG.arrayClone(this.original_data);if(prop['chart.accumulative']){for(var i=0;i<this.data.length;++i){if(this.data[i].length!=this.data[0].length){alert('[RADAR] Error! When the radar has chart.accumulative set to true all the datasets must have the same number of elements');}}}
if(RG.isNull(prop['chart.labels.axes.boxed'])){prop['chart.labels.axes.boxed']=[];for(var i=0;i<((RG.isArray(prop['chart.labels.axes.specific'])&&prop['chart.labels.axes.specific'].length)||prop['chart.labels.axes.count']||5);++i){prop['chart.labels.axes.boxed'][i]=false;}}
this.marginLeft=prop['chart.margin.left'];this.marginRight=prop['chart.margin.right'];this.marginTop=prop['chart.margin.top'];this.marginBottom=prop['chart.margin.bottom'];this.centerx=((ca.width-this.marginLeft-this.marginRight)/2)+this.marginLeft;this.centery=((ca.height-this.marginTop-this.marginBottom)/2)+this.marginTop;this.radius=Math.min(ca.width-this.marginLeft-this.marginRight,ca.height-this.marginTop-this.marginBottom)/2;if(typeof prop['chart.centerx']=='number')this.centerx=2*prop['chart.centerx'];if(typeof prop['chart.centery']=='number')this.centery=2*prop['chart.centery'];if(typeof prop['chart.radius']=='number')this.radius=prop['chart.radius'];if(!this.colorsParsed){this.parseColors();this.colorsParsed=true;}
if(!prop['chart.scale.max']){if(prop['chart.accumulative']){var accumulation=[];var len=this.original_data[0].length
for(var i=1;i<this.original_data.length;++i){if(this.original_data[i].length!=len){alert('[RADAR] Error! Stacked Radar chart datasets must all be the same size!');}
for(var j=0;j<this.original_data[i].length;++j){this.data[i][j]+=this.data[i-1][j];this.max=Math.max(this.max,this.data[i][j]);}}}
this.scale2=RG.getScale2(this,{'scale.max':typeof prop['chart.scale.max']==='number'?prop['chart.scale.max']:this.max,'scale.min':0,'scale.decimals':Number(prop['chart.scale.decimals']),'scale.point':prop['chart.scale.point'],'scale.thousand':prop['chart.scale.thousand'],'scale.round':prop['chart.scale.round'],'scale.units.pre':prop['chart.scale.units.pre'],'scale.units.post':prop['chart.scale.units.post'],'scale.labels.count':prop['chart.labels.axes.count']});this.max=this.scale2.max;}else{var ymax=prop['chart.scale.max'];this.scale2=RG.getScale2(this,{'scale.max':ymax,'scale.min':0,'scale.strict':true,'scale.decimals':Number(prop['chart.scale.decimals']),'scale.point':prop['chart.scale.point'],'scale.thousand':prop['chart.scale.thousand'],'scale.round':prop['chart.scale.round'],'scale.units.pre':prop['chart.scale.units.pre'],'scale.units.post':prop['chart.scale.units.post'],'scale.labels.count':prop['chart.labels.axes.count']});this.max=this.scale2.max;}
this.drawBackground();this.drawAxes();this.drawCircle();this.drawLabels();co.save();co.beginPath();co.arc(this.centerx,this.centery,this.radius*2,-RG.HALFPI,(RG.TWOPI*prop['chart.animation.trace.clip'])-RG.HALFPI,false);co.lineTo(this.centerx,this.centery);co.closePath();co.clip();this.DrawChart();this.DrawHighlights();co.restore();this.drawAxisLabels();if(prop['chart.title']){RG.drawTitle(this,prop['chart.title'],this.marginTop,null,null)}
if(prop['chart.key']){RG.drawKey(this,prop['chart.key'],prop['chart.colors']);}
if(prop['chart.contextmenu']){RG.showContext(this);}
if(prop['chart.resizable']){RG.allowResizing(this);}
RG.installEventListeners(this);if((prop['chart.fill.click']||prop['chart.fill.mousemove']||!RG.isNull(prop['chart.fill.tooltips']))&&!this.__fill_click_listeners_installed__){this.addFillListeners();this.__fill_click_listeners_installed__=true;}
if(this.firstDraw){this.firstDraw=false;RG.fireCustomEvent(this,'onfirstdraw');this.firstDrawFunc();}
RGraph.fireCustomEvent(this,'ondraw');return this;};this.exec=function(func)
{func(this);return this;};this.drawBackground=this.DrawBackground=function()
{var color=prop['chart.background.circles.color'];var poly=prop['chart.background.circles.poly'];var spacing=prop['chart.background.circles.spacing'];var spokes=prop['chart.background.circles.spokes'];co.lineWidth=1;if(prop['chart.background.circles']&&poly==false){co.strokeStyle=color;co.beginPath();var numrings=typeof(prop['chart.background.circles.count'])=='number'?prop['chart.background.circles.count']:prop['chart.labels.axes.count'];for(var r=0;r<=this.radius;r+=(this.radius/numrings)){co.moveTo(this.centerx,this.centery);co.arc(this.centerx,this.centery,r,0,RG.TWOPI,false);}
co.stroke();co.strokeStyle=color;for(var i=0;i<360;i+=(360/spokes)){co.beginPath();co.arc(this.centerx,this.centery,this.radius,(i/360)*RG.TWOPI,((i+0.001)/360)*RG.TWOPI,false);co.lineTo(this.centerx,this.centery);co.stroke();}}else if(prop['chart.background.circles']&&poly==true){co.strokeStyle=color;var increment=360/this.data[0].length
for(var i=0;i<360;i+=increment){co.beginPath();co.arc(this.centerx,this.centery,this.radius,((i/360)*RG.TWOPI)-RG.HALFPI,(((i+0.001)/360)*RG.TWOPI)-RG.HALFPI,false);co.lineTo(this.centerx,this.centery);co.stroke();}
co.strokeStyle=color;var numrings=typeof prop['chart.background.circles.count']==='number'?prop['chart.background.circles.count']:prop['chart.labels.axes.count'];for(var r=0;r<=this.radius;r+=(this.radius/numrings)){co.beginPath();for(var a=0;a<=360;a+=(360/this.data[0].length)){co.arc(this.centerx,this.centery,r,RG.toRadians(a)-RG.HALFPI,RG.toRadians(a)+0.001-RG.HALFPI,false);}
co.closePath();co.stroke();}}};this.drawAxes=this.DrawAxes=function()
{co.strokeStyle=prop['chart.axes.color'];co.lineWidth=prop['chart.axes.linewidth'];var halfsize=this.radius;co.beginPath();co.moveTo(Math.round(this.centerx),this.centery+this.radius);co.lineTo(Math.round(this.centerx),this.centery-this.radius);co.moveTo(this.centerx-5,Math.round(this.centery+this.radius));co.lineTo(this.centerx+5,Math.round(this.centery+this.radius));co.moveTo(this.centerx-5,Math.round(this.centery-this.radius));co.lineTo(this.centerx+5,Math.round(this.centery-this.radius));for(var y=(this.centery-this.radius);y<(this.centery+this.radius);y+=(this.radius/prop['chart.yaxis.tickmarks.count'])){co.moveTo(this.centerx-3,Math.round(y));co.lineTo(this.centerx+3,Math.round(y));}
co.moveTo(this.centerx-this.radius,Math.round(this.centery));co.lineTo(this.centerx+this.radius,Math.round(this.centery));co.moveTo(Math.round(this.centerx-this.radius),this.centery-5);co.lineTo(Math.round(this.centerx-this.radius),this.centery+5);co.moveTo(Math.round(this.centerx+this.radius),this.centery-5);co.lineTo(Math.round(this.centerx+this.radius),this.centery+5);for(var x=(this.centerx-this.radius);x<(this.centerx+this.radius);x+=(this.radius/prop['chart.xaxis.tickmarks.count'])){co.moveTo(Math.round(x),this.centery-3);co.lineTo(Math.round(x),this.centery+3);}
co.stroke();};this.drawChart=this.DrawChart=function()
{var alpha=prop['chart.colors.alpha'];if(typeof(alpha)=='number'){var oldAlpha=co.globalAlpha;co.globalAlpha=alpha;}
var numDatasets=this.data.length;for(var dataset=0;dataset<this.data.length;++dataset){co.beginPath();var coords_dataset=[];for(var i=0;i<this.data[dataset].length;++i){var coords=this.GetCoordinates(dataset,i);if(coords_dataset==null){coords_dataset=[];}
coords_dataset.push(coords);this.coords.push(coords);}
this.coords2[dataset]=coords_dataset;co.strokeStyle=(typeof(prop['chart.colors.stroke'])=='object'&&prop['chart.colors.stroke'][dataset])?prop['chart.colors.stroke'][dataset]:prop['chart.colors.stroke'];co.fillStyle=prop['chart.colors'][dataset]?prop['chart.colors'][dataset]:'rgba(0,0,0,0)';if(co.fillStyle==='transparent'){co.fillStyle='rgba(0,0,0,0)';}
co.lineWidth=prop['chart.linewidth'];for(i=0;i<coords_dataset.length;++i){if(i==0){co.moveTo(coords_dataset[i][0],coords_dataset[i][1]);}else{co.lineTo(coords_dataset[i][0],coords_dataset[i][1]);}}
if(prop['chart.accumulative']&&dataset>0){co.lineTo(coords_dataset[0][0],coords_dataset[0][1]);co.moveTo(last_coords[0][0],last_coords[0][1]);for(var i=coords_dataset.length-1;i>=0;--i){co.lineTo(last_coords[i][0],last_coords[i][1]);}}
var last_coords=coords_dataset;co.closePath();co.fill();co.stroke();}
if(typeof(alpha)=='number'){co.globalAlpha=oldAlpha;}};this.getCoordinates=this.GetCoordinates=function(dataset,index)
{var len=this.data[dataset].length;var mag=(this.data[dataset][index]/this.max)*this.radius;var angle=(RG.TWOPI/len)*index;angle-=RG.HALFPI;var x=Math.cos(angle)*mag;var y=Math.sin(angle)*mag;x=this.centerx+x;y=this.centery+y;return[x,y];};this.drawLabels=this.DrawLabels=function()
{var labels=prop['chart.labels'];if(labels&&labels.length>0){co.lineWidth=1;co.strokeStyle='gray';co.fillStyle=prop['chart.labels.color']||prop['chart.text.color'];var bgFill=prop['chart.labels.background.fill'],bold=prop['chart.labels.bold'],bgBoxed=prop['chart.labels.boxed'],offset=prop['chart.labels.offset'],font=prop['chart.text.font'],size=prop['chart.text.size'],radius=this.radius,color=prop['chart.labels.color']||prop['chart.text.color']
for(var i=0;i<labels.length;++i){var angle=(RG.TWOPI/prop['chart.labels'].length)*i;angle-=RG.HALFPI;var x=this.centerx+(ma.cos(angle)*(radius+offset));var y=this.centery+(ma.sin(angle)*(radius+offset));var halign=x<this.centerx?'right':'left';if(i==0||(i/labels.length)==0.5)halign='center';if(labels[i]&&labels[i].length){var textConf=RG.getTextConf({object:this,prefix:'chart.labels'});RG.text2(this,{font:textConf.font,size:textConf.size,color:textConf.color,bold:textConf.bold,italic:textConf.italic,x:x,y:y,text:labels[i],valign:'center',halign:halign,bounding:bgBoxed,boundingFill:bgFill,tag:'labels'});}}}};this.drawCircle=this.DrawCircle=function()
{var circle={};circle.limit=prop['chart.circle'];circle.fill=prop['chart.circle.fill'];circle.stroke=prop['chart.circle.stroke'];if(circle.limit){var r=(circle.limit/this.max)*this.radius;co.fillStyle=circle.fill;co.strokeStyle=circle.stroke;co.beginPath();co.arc(this.centerx,this.centery,r,0,RG.TWOPI,0);co.fill();co.stroke();}};this.drawAxisLabels=this.DrawAxisLabels=function()
{if(RG.isArray(prop['chart.labels.axes.specific'])&&prop['chart.labels.axes.specific'].length){this.drawSpecificAxisLabels();return;}
co.lineWidth=1;co.fillStyle='black';co.strokeStyle='black';var r=this.radius,font=prop['chart.text.font'],axes=prop['chart.labels.axes'].toLowerCase(),color=prop['chart.labels.axes.boxed.background'],drawzero=false,units_pre=prop['chart.scale.units.pre'],units_post=prop['chart.scale.units.post'],decimals=prop['chart.scale.decimals'],bold=prop['chart.labels.axes.bold'],boxed=prop['chart.labels.axes.boxed'],centerx=this.centerx,centery=this.centery,scale=this.scale;co.fillStyle=prop['chart.text.color'];var textConf=RG.getTextConf({object:this,prefix:'chart.labels.axes'});if(axes.indexOf('n')>-1){for(var i=0;i<this.scale2.labels.length;++i){RG.text2(this,{font:textConf.font,size:textConf.size,color:textConf.color,bold:textConf.bold,italic:textConf.italic,x:centerx,y:centery-(r*((i+1)/this.scale2.labels.length)),text:this.scale2.labels[i],valign:'center',halign:'center',bounding:boxed[i]||color,boundingFill:color,boundingStroke:'rgba(0,0,0,0)',tag:'scale'});}
drawzero=true;}
if(axes.indexOf('s')>-1){for(var i=0;i<this.scale2.labels.length;++i){RG.text2(this,{font:textConf.font,size:textConf.size,color:textConf.color,bold:textConf.bold,italic:textConf.italic,x:centerx,y:centery+(r*((i+1)/this.scale2.labels.length)),text:this.scale2.labels[i],valign:'center',halign:'center',bounding:boxed[i]||color,boundingFill:color,boundingStroke:'rgba(0,0,0,0)',tag:'scale'});}
drawzero=true;}
if(axes.indexOf('e')>-1){for(var i=0;i<this.scale2.labels.length;++i){RG.text2(this,{font:textConf.font,size:textConf.size,color:textConf.color,bold:textConf.bold,italic:textConf.italic,x:centerx+(r*((i+1)/this.scale2.labels.length)),y:centery,text:this.scale2.labels[i],valign:'center',halign:'center',bounding:boxed[i]||color,boundingFill:color,boundingStroke:'rgba(0,0,0,0)',tag:'scale'});}
drawzero=true;}
if(axes.indexOf('w')>-1){for(var i=0;i<this.scale2.labels.length;++i){RG.text2(this,{font:textConf.font,size:textConf.size,color:textConf.color,bold:textConf.bold,italic:textConf.italic,x:centerx-(r*((i+1)/this.scale2.labels.length)),y:centery,text:this.scale2.labels[i],valign:'center',halign:'center',bounding:boxed[i]||color,boundingFill:color,boundingStroke:'rgba(0,0,0,0)',tag:'scale'});}
drawzero=true;}
if(drawzero){RG.Text2(this,{font:textConf.font,size:textConf.size,color:textConf.color,bold:textConf.bold,italic:textConf.italic,x:centerx,y:centery,text:RG.numberFormat({object:this,number:Number(0).toFixed(),unitspre:units_pre,unitspost:units_post}),valign:'center',halign:'center',bounding:color?true:false,boundingFill:color,boundingStroke:'rgba(0,0,0,0)',tag:'scale'});}};this.drawSpecificAxisLabels=this.DrawSpecificAxisLabels=function()
{var labels=prop['chart.labels.axes.specific'];var bold=RG.arrayPad(prop['chart.labels.axes.bold'],labels.length);var boxed=RG.arrayPad(prop['chart.labels.axes.boxed'],labels.length);var reversed_labels=RG.arrayReverse(labels);var reversed_bold=RG.arrayReverse(bold);var reversed_boxed=RG.arrayReverse(boxed);var font=prop['chart.text.font'];var axes=prop['chart.labels.axes'].toLowerCase();co.fillStyle=prop['chart.text.color'];var textConf=RG.getTextConf({object:this,prefix:'chart.labels.axes'});for(var i=0;i<labels.length;++i){if(axes.indexOf('n')>-1)RG.text2(this,{font:textConf.font,size:textConf.size,color:textConf.color,bold:textConf.bold,italic:textConf.italic,tag:'labels.axes.specific',x:this.centerx,y:this.centery-this.radius+((this.radius/labels.length)*i),text:reversed_labels[i],valign:'center',halign:'center',bounding:reversed_boxed[i],boundingFill:'white'});if(axes.indexOf('s')>-1)RG.text2(this,{font:textConf.font,size:textConf.size,color:textConf.color,bold:textConf.bold,italic:textConf.italic,tag:'labels.axes.specific',x:this.centerx,y:this.centery+((this.radius/labels.length)*(i+1)),text:labels[i],valign:'center',halign:'center',bounding:boxed[i],boundingFill:'white'});if(axes.indexOf('w')>-1)RG.text2(this,{font:textConf.font,size:textConf.size,color:textConf.color,bold:textConf.bold,italic:textConf.italic,tag:'labels.axes.specific',x:this.centerx-this.radius+((this.radius/labels.length)*i),y:this.centery,text:reversed_labels[i],valign:'center',halign:'center',bounding:reversed_boxed[i],boundingFill:'white'});if(axes.indexOf('e')>-1)RG.text2(this,{font:textConf.font,size:textConf.size,color:textConf.color,bold:textConf.bold,italic:textConf.italic,tag:'labels.axes.specific',x:this.centerx+((this.radius/labels.length)*(i+1)),y:this.centery,text:labels[i],valign:'center',halign:'center',bounding:boxed[i],boundingFill:'white'});}};this.getShape=this.getPoint=function(e)
{for(var i=0;i<this.coords.length;++i){var x=this.coords[i][0];var y=this.coords[i][1];var tooltips=prop['chart.tooltips'];var index=Number(i);var mouseXY=RG.getMouseXY(e);var mouseX=mouseXY[0];var mouseY=mouseXY[1];if(mouseX<(x+5)&&mouseX>(x-5)&&mouseY>(y-5)&&mouseY<(y+5)){var tooltip=RG.parseTooltipText(prop['chart.tooltips'],index);return{0:this,'object':this,1:x,'x':x,2:y,'y':y,3:null,'dataset':null,4:index,'index':i,'tooltip':tooltip}}}};this.highlight=this.Highlight=function(shape)
{if(typeof prop['chart.highlight.style']==='function'){(prop['chart.highlight.style'])(shape);}else{RG.Highlight.Point(this,shape);}};this.getObjectByXY=function(e)
{var mouseXY=RG.getMouseXY(e);if(mouseXY[0]>(this.centerx-this.radius)&&mouseXY[0]<(this.centerx+this.radius)&&mouseXY[1]>(this.centery-this.radius)&&mouseXY[1]<(this.centery+this.radius)){return this;}};this.drawHighlights=this.DrawHighlights=function()
{if(prop['chart.highlights']){var sequentialIdx=0;var dataset=0;var index=0;var radius=prop['chart.highlights.radius'];for(var dataset=0;dataset<this.data.length;++dataset){for(var index=0;index<this.data[dataset].length;++index){co.beginPath();co.strokeStyle=prop['chart.highlights.stroke'];co.fillStyle=prop['chart.highlights.fill']?prop['chart.highlights.fill']:((typeof(prop['chart.colors.stroke'])=='object'&&prop['chart.colors.stroke'][dataset])?prop['chart.colors.stroke'][dataset]:prop['chart.colors.stroke']);co.arc(this.coords[sequentialIdx][0],this.coords[sequentialIdx][1],radius,0,RG.TWOPI,false);co.stroke();co.fill();++sequentialIdx;}}}};this.getRadius=function(value)
{if(value<0||value>this.max){return null;}
var radius=(value/this.max)*this.radius;return radius;};this.getAngle=function(numitems,index)
{var angle=(RG.TWOPI/numitems)*index;angle-=RG.HALFPI;return angle;};this.parseColors=function()
{if(this.original_colors.length===0){this.original_colors['chart.colors']=RG.arrayClone(prop['chart.colors']);this.original_colors['chart.key.colors']=RG.arrayClone(prop['chart.key.colors']);this.original_colors['chart.title.color']=RG.arrayClone(prop['chart.title.color']);this.original_colors['chart.text.color']=RG.arrayClone(prop['chart.text.color']);this.original_colors['chart.labels.color']=RG.arrayClone(prop['chart.labels.color']);this.original_colors['chart.labels.axes.color']=RG.arrayClone(prop['chart.labels.axes.color']);this.original_colors['chart.highlight.stroke']=RG.arrayClone(prop['chart.highlight.stroke']);this.original_colors['chart.highlight.fill']=RG.arrayClone(prop['chart.highlight.fill']);this.original_colors['chart.circle.fill']=RG.arrayClone(prop['chart.circle.fill']);this.original_colors['chart.circle.stroke']=RG.arrayClone(prop['chart.circle.stroke']);this.original_colors['chart.colors.stroke']=RG.arrayClone(prop['chart.colors.stroke']);}
for(var i=0;i<prop['chart.colors'].length;++i){prop['chart.colors'][i]=this.parseSingleColorForGradient(prop['chart.colors'][i]);}
var keyColors=prop['chart.key.colors'];if(typeof(keyColors)!='null'&&keyColors&&keyColors.length){for(var i=0;i<prop['chart.key.colors'].length;++i){prop['chart.key.colors'][i]=this.parseSingleColorForGradient(prop['chart.key.colors'][i]);}}
prop['chart.title.color']=this.parseSingleColorForGradient(prop['chart.title.color']);prop['chart.text.color']=this.parseSingleColorForGradient(prop['chart.text.color']);prop['chart.labels.color']=this.parseSingleColorForGradient(prop['chart.labels.color']);prop['chart.labels.axes.color']=this.parseSingleColorForGradient(prop['chart.labels.axes.color']);prop['chart.highlight.stroke']=this.parseSingleColorForGradient(prop['chart.highlight.stroke']);prop['chart.highlight.fill']=this.parseSingleColorForGradient(prop['chart.highlight.fill']);prop['chart.circle.fill']=this.parseSingleColorForGradient(prop['chart.circle.fill']);prop['chart.circle.stroke']=this.parseSingleColorForGradient(prop['chart.circle.stroke']);if(typeof prop['chart.colors.stroke']==='object'){for(var i=0;i<prop['chart.colors.stroke'].length;++i){prop['chart.colors.stroke'][i]=this.parseSingleColorForGradient(prop['chart.colors.stroke'][i]);}}else{prop['chart.colors.stroke']=this.parseSingleColorForGradient(prop['chart.colors.stroke']);}};this.reset=function()
{};this.parseSingleColorForGradient=function(color)
{if(!color||typeof(color)!='string'){return color;}
if(color.match(/^gradient\((.*)\)$/i)){if(color.match(/^gradient\(({.*})\)$/i)){return RGraph.parseJSONGradient({object:this,def:RegExp.$1});}
var parts=RegExp.$1.split(':');var grad=co.createRadialGradient(this.centerx,this.centery,0,this.centerx,this.centery,this.radius);var diff=1/(parts.length-1);grad.addColorStop(0,RG.trim(parts[0]));for(var j=1;j<parts.length;++j){grad.addColorStop(j*diff,RG.trim(parts[j]));}}
return grad?grad:color;};this.addFillListeners=this.AddFillListeners=function(e)
{var obj=this;var func=function(e)
{var coords=this.coords;var coords2=this.coords2;var mouseXY=RG.getMouseXY(e);var dataset=0;if(e.type=='mousemove'&&prop['chart.fill.mousemove.redraw']){RG.RedrawCanvas(ca);}
for(var dataset=(obj.coords2.length-1);dataset>=0;--dataset){co.beginPath();co.moveTo(obj.coords2[dataset][0][0],obj.coords2[dataset][0][1]);for(var j=0;j<obj.coords2[dataset].length;++j){co.lineTo(obj.coords2[dataset][j][0],obj.coords2[dataset][j][1]);}
co.lineTo(obj.coords2[dataset][0][0],obj.coords2[dataset][0][1]);if(prop['chart.accumulative']&&dataset>0){co.lineTo(obj.coords2[dataset-1][0][0],obj.coords2[dataset-1][0][1]);for(var j=(obj.coords2[dataset-1].length-1);j>=0;--j){co.lineTo(obj.coords2[dataset-1][j][0],obj.coords2[dataset-1][j][1]);}}
co.closePath();if(co.isPointInPath(mouseXY[0],mouseXY[1])){var inPath=true;break;}}
if(inPath){var fillTooltips=prop['chart.fill.tooltips'];if(e.type=='click'){if(prop['chart.fill.click']){prop['chart.fill.click'](e,dataset);}
if(prop['chart.fill.tooltips']&&prop['chart.fill.tooltips'][dataset]){obj.DatasetTooltip(e,dataset);}}
if(e.type=='mousemove'){if(prop['chart.fill.mousemove']){prop['chart.fill.mousemove'](e,dataset);}
if(!RG.is_null(fillTooltips)){e.target.style.cursor='pointer';}
if(prop['chart.fill.tooltips']&&prop['chart.fill.tooltips'][dataset]){e.target.style.cursor='pointer';}}
e.stopPropagation();}else if(e.type=='mousemove'){ca.style.cursor='default';}};if(prop['chart.fill.click']||!RG.is_null(prop['chart.fill.tooltips'])){ca.addEventListener('click',func,false);}
if(prop['chart.fill.mousemove']||!RG.isNull(prop['chart.fill.tooltips'])){ca.addEventListener('mousemove',func,false);}};this.highlightDataset=this.HighlightDataset=function(dataset)
{co.beginPath();for(var j=0;j<this.coords2[dataset].length;++j){if(j==0){co.moveTo(this.coords2[dataset][0][0],this.coords2[dataset][0][1]);}else{co.lineTo(this.coords2[dataset][j][0],this.coords2[dataset][j][1]);}}
co.lineTo(this.coords2[dataset][0][0],this.coords2[dataset][0][1]);if(prop['chart.accumulative']&&dataset>0){co.lineTo(this.coords2[dataset-1][0][0],this.coords2[dataset-1][0][1]);for(var j=(this.coords2[dataset-1].length-1);j>=0;--j){co.lineTo(this.coords2[dataset-1][j][0],this.coords2[dataset-1][j][1]);}}
co.strokeStyle=prop['chart.fill.highlight.stroke'];co.fillStyle=prop['chart.fill.highlight.fill'];co.stroke();co.fill();};this.datasetTooltip=this.DatasetTooltip=function(e,dataset)
{this.HighlightDataset(dataset);var text=prop['chart.fill.tooltips'][dataset];var x=0;var y=this.coords2[dataset][0][1]+RG.getCanvasXY(ca)[1];RG.Tooltip(this,text,x,y,0,e);};this.interactiveKeyHighlight=function(index)
{var coords=this.coords2[index];if(coords){var pre_linewidth=co.lineWidth;var pre_linecap=co.lineCap;co.lineWidth=prop['chart.linewidth']+10;co.lineCap='round';co.strokeStyle=prop['chart.key.interactive.highlight.chart.stroke'];co.beginPath();for(var i=0,len=coords.length;i<len;i+=1){if(i==0){co.moveTo(coords[i][0],coords[i][1]);}else{co.lineTo(coords[i][0],coords[i][1]);}}
co.closePath();co.stroke();co.lineWidth=pre_linewidth;co.lineCap=pre_linecap;}};this.on=function(type,func)
{if(type.substr(0,2)!=='on'){type='on'+type;}
if(typeof this[type]!=='function'){this[type]=func;}else{RG.addCustomEventListener(this,type,func);}
return this;};this.firstDrawFunc=function()
{};this.grow=function()
{var obj=this;var callback=arguments[1]?arguments[1]:function(){};var opt=arguments[0]?arguments[0]:{};var frames=opt.frames?opt.frames:30;var frame=0;var data=RG.array_clone(obj.data);function iterator()
{for(var i=0,len=data.length;i<len;++i){for(var j=0,len2=data[i].length;j<len2;++j){obj.original_data[i][j]=(frame/frames)*data[i][j];}}
RGraph.clear(obj.canvas);RGraph.redrawCanvas(obj.canvas);if(frame<frames){frame++;RGraph.Effects.updateCanvas(iterator);}else{callback(obj);}}
iterator();return this;};this.trace=function()
{var obj=this;var opt=arguments[0]||{};var frames=opt.frames||60;var frame=0;var callback=arguments[1]||function(){};obj.set('chart.animation.trace.clip',0);var iterator=function()
{if(frame<frames){obj.set('chart.animation.trace.clip',frame/frames);frame++;RG.redrawCanvas(obj.canvas);RG.Effects.updateCanvas(iterator);}else{obj.set('chart.animation.trace.clip',1);RG.redrawCanvas(obj.canvas);callback(obj);}};iterator();return this;};RG.register(this);if(parseConfObjectForOptions){RG.parseObjectStyleConfig(this,conf.options);}};