/***************************************************	

	fValidate
	Copyright (c) 2000-2003
	by Peter Bailey
	www.peterbailey.net/fValidate/

	fValidate.datetime.js

	Included Validators
	-------------------
	date

	This file is only part of a larger validation
	library	and will not function autonomously.

	Created at a tab-spacing of four (4)

****************************************************/

fValidate.prototype.time = function( formatStr, delim, code, specDate )
{
	if ( this.typeMismatch( 'text' ) ) return;
	if ( typeof formatStr == 'undefined' )
	{
		this.paramError( 'formatStr' );
		return;
	}

	var error	= 1;

	var reg = /^(\d{1,2}):(\d{1,2}):(\d{1,2})$/; 
  var r = this.elem.value.match(reg);   
  if(r==null){  	  	
  	var reg = /^(\d{1,2}):(\d{1,2})$/; 
    var r = this.elem.value.match(reg);   
    if(r==null){  	  	
    	this.throwError( [this.elem.fName] );
    	return;
    } 
    var d= new Date(0, 0, 0, r[1] ,r[2] , 0);
    if(parseInt(r[1], 10) == d.getHours() && parseInt(r[2], 10) == d.getMinutes()){
    	this.elem.value = this.elem.value + ":00";
    }else{  	
  	  this.throwError( [this.elem.fName] );
  	}
  	return;
  } 
  var d= new Date(0, 0, 0, r[1] ,r[2] ,r[3]);

  if(parseInt(r[1], 10) == d.getHours() && parseInt(r[2], 10) == d.getMinutes() && parseInt(r[3], 10) == d.getSeconds()){
  }else{  	
   this.throwError( [this.elem.fName] );
  }  
  return;
}	
//	EOF
