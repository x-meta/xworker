/*path:xworker.swt.SWT/@actions/@isRTW*/
package xworker.swt.SWT.p_211087288;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.WebClient;

if( RWT.getClient() instanceof WebClient ) {
    return true;			
}else{
    return false;
}