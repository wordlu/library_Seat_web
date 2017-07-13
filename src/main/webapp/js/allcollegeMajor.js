/*
*	学院专业班级三级联动
*/
function Dsy(){
	this.Items = {};
}
Dsy.prototype.add = function(id,iArray){
	this.Items[id] = iArray;
}
Dsy.prototype.Exists = function(id){
	if(typeof(this.Items[id]) == "undefined") return false;
	return true;
}

function change(v){
	var str="0";
	for(i=0;i<v;i++){
		str+=("_"+(document.getElementById(s[i]).selectedIndex-1));
	};
	var ss=document.getElementById(s[v]);
	with(ss){
		length = 0;
		options[0]=new Option(opt0[v],opt0[v]);
		if(v && document.getElementById(s[v-1]).selectedIndex>0 || !v){
			if(dsy.Exists(str)){
				ar = dsy.Items[str];
				for(i=0;i<ar.length;i++){
					options[length]=new Option(ar[i],ar[i]);
				}//end for
				if(v){ options[0].selected = true; }
			}
		}//end if v
		if(++v<s.length){change(v);}
	}//End with
}

var dsy = new Dsy();

dsy.add("0",["北京市","天津市","上海市"]);
dsy.add("0_0_0",["东城区","西城区","崇文区"]);
dsy.add("0_0",["北京市"]);
dsy.add("0_1_0",["和平区","河东区","河西区"]);
dsy.add("0_1",["天津市"]);
dsy.add("0_2_0",["黄浦区","卢湾区","徐汇区"]);
dsy.add("0_2",["上海市"]);
dsy.add("0",["北京市","天津市","上海市"]);

var s=["s_college","s_major","s_class"];//三个select的name
var opt0 = ["学院","专业","班级"];//初始值
function _init_collegeMajor(){  //初始化函数
	for(i=0;i<s.length-1;i++){
	  document.getElementById(s[i]).onchange=new Function("change("+(i+1)+")");
	}
	change(0);
}