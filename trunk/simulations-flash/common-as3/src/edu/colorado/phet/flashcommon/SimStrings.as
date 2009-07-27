﻿package edu.colorado.phet.flashcommon {
	
	import flash.display.LoaderInfo;
	
	public class SimStrings {
		private static var document : XML;;
		
		public static function init( li : LoaderInfo ) {
			document = new XML( li.parameters.internationalization );		
		}
		
		public static function get( key : String, defaultString : String ) : String {
			var list : XMLList = document.descendants( "string" );
			
			for each ( var item : XML in list ) {				
				if( item.attribute( "key" ).toString() == key ) {
					return item.attribute( "value" ).toString();
				}
			}
			
			return defaultString;
		}
	}
}