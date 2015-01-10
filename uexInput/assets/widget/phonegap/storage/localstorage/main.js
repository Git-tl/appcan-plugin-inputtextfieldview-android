// 等待加载PhoneGap
document.addEventListener("deviceready", onDeviceReady, false); 

// PhoneGap加载完毕
function onDeviceReady() {
   	window.localStorage.setItem("key", "value");
	var value = window.localStorage.getItem("key");
   	// value的值现在是“value”
	alert("key的值:"+value);

   	window.localStorage.removeItem("key");
   	window.localStorage.setItem("key2", "value2");
   	window.localStorage.clear();
	alert("调用clear方法之后,key的值:"+localStorage.getItem("key2"));
	   	// localStorage现在是空的
}
