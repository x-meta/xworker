/*********************************************************
*
*--	fValidate US-English language file.
*
*	Translation by: Peter Bailey
*	Email: me@peterbailey.net
*
*	Visit http://www.peterbailey.net/fValidate/api/i18n/
*	for additional translations and instructions on
*	making your own translation.
*
*	!!! WARNING !!! Changing anything but the literal 
*	strings will likely cause script failure!
*
*	Note: This document most easily read/edited with tab-
*	spacing set to 4
*
*********************************************************/

if ( typeof fvalidate == 'undefined' )
{
	var fvalidate = new Object();
}

fvalidate.i18n =
{
	//	Validation errors
	errors:
	{
		blank:		[
			["请输入", 0]
			],
		length:		[
			[0, "必须至少", 1, "字节长度。"],
			[0, " 必须最多", 1, " 字节长度.\n当前的文本是", 2, "字节长度。"]
			],
		equalto:	[
			[0, "必须等于", 1]
			],
		number:		[
			["您为", 0, "输入的数字不是有效的。"]
			],
		numeric:	[
			[0, "只允许有效的数字"],
			[1, "最小值为", 0]
			],
		alnum:		[
			["您输入的数据，\"", 0, "\"，不符合需要的格式", 1,  
			"\n最小长度为: ", 2,
			"\n条件: ", 3,
			"\n允许以下数字: ", 4,
			"\n允许的数字范围: ", 5,
			"\n允许的标点符号为: ", 6, "\n"]
			],
		decimal:	[
			["您输入的数据，", 0, "不是有效的。请重新输入", 1]
			],
		decimalr:	[
			[0, "不是有效的。请重新输入"]
			],
		ip:			[
			["请输入有效的IP地址"],
			["您输入的端口，", 0, "，超出范围。\n端口必须在", 1, "和", 2, "之间。"]
			],
		ssn:		[
			["你必须输入有效的社会保险代码。\n您的SSN必须符合'XXX-XX-XXXX'格式。"]
			],
		money:		[
			[0, "不符合需要的格式", 1]
			],
		cc:			[
			["The ", 0, "您输入的数据是无效的。请检查并重新输入。"]
			],
		ccDate:		[
			["您的信用卡过期了！请重新输入另外一张有效的信用卡号码。"]
			],
		zip:		[
			["请输入5或9位的邮编。"]
			],
		phone:		[
			["请输入有效的电话号码，最好带有区号。"],
			["请输入有效的电话号码-7或10位有效的数字。"]
			],
		email:		[
			["请输入有效的电子邮件地址。"]
			],
		url:		[
			[0, " 不是有效的域名"]
			],
		date:		[
			["您输入的数据", 0, "不是有效的日期\n请用以下格式输入一个日期: ", 1],
			["日期必须在", 0, "之前"],
			["日期必须是或在", 0, "之前"],
			["日期必须在", 0, "之后"],
			["日期必须是或在", 0, "之后"]
			],
	  time: [
	    ["请输入有效的时间。"]
	    ],
		select:		[
			["请为", 0, "选择有效的选项"]
			],
		selectm:	[
			["请为", 2, "选择在", 0, "和", 1, "之间的选项。\n您当前有", 3, "选择了"]
			],
		selecti:	[
			["请为", 0, "选择一个有效的选项"]
			],
		checkbox:	[
			["请在继续之前校验", 0],
			["请为", 2, "选择在", 0, "和", 1, "之间的选项\n您当前有", 3, "选择了"]
			],
		radio:		[
			["请在继续之前校验", 0],
			["请为", 0, "选择一个选项"]
			],
		comparison:	[
			[0, "必须是", 1, "", 2]
			],
		eitheror:	[
			["至少有一个且有一个以下的字段必须要输入:\n\t-", 0, "\n"]
			],
		atleast:	[
			["最少有", 0, " 以下字段必须要输入:\n\t-", 1, "\n\n您当前仅输入了以下", 2, "字段。\n"]
			],
		allornone:	[
			["以下字段必须全部输入正确或者全部不要输入:\n\t-", 0, "\n您当然仅有以下", 1, "字段正确输入了。\n"]
			],
		file:		[
			["文件类型必须是以下几种？\n", 0, "\n备注: 文件扩展名可能区分大小写。"]
			],
		custom:		[
			[0, "不是有效的。"]
			],
		cazip:		[
			["请输入有效的邮政编码。"]
			],
		ukpost:		[
			["请输入有效的邮政编码。"]
			],
		germanpost:	[
			["请输入有效的邮政编码。"]
			],
		swisspost:	[
			["请输入有效的邮政编码。"]
			]
	},

	comparison:
	{
		gt:		"大于",
		lt:		"小于",
		gte:	"大于等于",
		lte:	"小于等于",
		eq:		"等于",
		neq:	"不等于"
	},

	//	Developer assist errors
	devErrors:
	{
		number:		["最小范围(", 0, ")大于最大范围(", 1, ")在这个元素: ", 2],
		length:		["最小值(", 0, ")大于最大值(", 1, ")在这个元素: ", 2],
		cc:			["信用卡类型(", 0, ")没有发现。"],

		lines:		["! 警告 ! -- fValidate 开发辅助错误\n", "\n如果您不是开发着， 请联系网站管理员阅读以下错误。"],
		paramError: ["您必须包含'", 0, "'参数为'", 1, "'有效的校验类型: ", 2],
		notFound:	["校验器'", 0, "'没有被发现。\n以下需要: ", 1],
		noLabel:	["没有发现标签元素: ", 0],
		noBox:		["唯有id为'", 0, "'的元素为'boxError'配置的值。"],
		missingName:["隐藏输入调用逻辑校验器必须有一个有效的名称或属性当用于联合'box'错误类型。\n\t", 0],
		mismatch:	["校验器/元素类型不匹配。\n\n元素: ", 0, "\n元素类型: ", 1, "\n校验器需要的类型: ", 2],
		noCCType:	["您必须有SELECT去选择信用卡类型！"]
	},

	//	Config values
	config :
	{
		confirmMsg :		"将要发送数据。\n请选择'确定'发送或者'取消'取消发送。",
		confirmAbortMsg :	"提交被取消了。数据没有被发送。"
	},

	//	Tooltip attached to Box-item errors
	boxToolTip:	"点击目标字段",

	//	Message displayed at top of alert error in group mode
	groupAlert:	"以下错误发生了:\n\n- ",

	//	Literal translation of the English 'or', include padding spaces.
	or:			"或"
}
