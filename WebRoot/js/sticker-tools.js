/**
 * By MHF 2017.1.7
 */
(function(window, document, undefined) {
	var oldSticker = window.Sticker,
		Sticker = {}
	Sticker.version = '0.1';
	Sticker.noConflict = function() {
		window.Sticker = oldSticker;
		return this;
	};

	window.Sticker = Sticker;
	Sticker.Toools = {
		/**
		 * 根据参数名获取参数
		 */
		getUrlParam : function(name) {
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
			var r = window.location.search.substr(1).match(reg);
			if (r != null) return unescape(r[2]);
			return null;
		},
		/**
		 * 获取url参数
		 */
		getUrlParams : function() {
			var url = location.search; //获取url中"?"符后的字串   
			var theRequest = new Object();
			if (url.indexOf("?") != -1) {
				var str = url.substr(1);
				strs = str.split("&");
				for (var i = 0; i < strs.length; i++) {
					theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
				}
			}
			return theRequest;
		},
		
		/**
		 * Json导出excel
		 */
		JSONToCSVConvertor : function(JSONData, ReportTitle, ShowLabel) {
			//调用示例start
			/* $("#btnExport").click(function() {  
			            var data = JSON.stringify($('#tablef').datagrid('getData').rows);  
			            alert(data);  
			            if (data == '')  
			                return;  
			  
			            JSONToCSVConvertor(data, "Download", true);  
			        });  */
			//调用示例end

			//If JSONData is not an object then JSON.parse will parse the JSON string in an Object  
			var arrData = typeof JSONData != 'object' ? JSON.parse(JSONData)
				: JSONData;

			var CSV = '';
			//Set Report title in first row or line  

			CSV += ReportTitle + '\r\n\n';

			//This condition will generate the Label/Header  
			if (ShowLabel) {
				var row = "";

				//This loop will extract the label from 1st index of on array  
				for (var index in arrData[0]) {

					//Now convert each value to string and comma-seprated  
					row += index + ',';
				}

				row = row.slice(0, -1);

				//append Label row with line break  
				CSV += row + '\r\n';
			}

			//1st loop is to extract each row  
			for (var i = 0; i < arrData.length; i++) {
				var row = "";

				//2nd loop will extract each column and convert it in string comma-seprated  
				for (var index in arrData[i]) {
					row += '"' + arrData[i][index] + '",';
				}

				row.slice(0, row.length - 1);

				//add a line break after each row  
				CSV += row + '\r\n';
			}

			if (CSV == '') {
				alert("Invalid data");
				return;
			}

			//Generate a file name  
			var fileName = "Sticker_";
			//this will remove the blank-spaces from the title and replace it with an underscore  
			fileName += ReportTitle.replace(/ /g, "_");

			//Initialize file format you want csv or xls  
			var uri = 'data:text/csv;charset=utf-8,' + escape(CSV);

			// Now the little tricky part.  
			// you can use either>> window.open(uri);  
			// but this will not work in some browsers  
			// or you will not get the correct file extension      

			//this trick will generate a temp <a /> tag  
			var link = document.createElement("a");
			link.href = uri;

			//set the visibility hidden so it will not effect on your web-layout  
			link.style = "visibility:hidden";
			link.download = fileName + ".csv";

			//this part will append the anchor tag and remove it after automatic click  
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
		}
	}
}(window, document));