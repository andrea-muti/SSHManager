package Thesis_Related.SSHManager;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * SSH TUNNEL MANAGER
 * 21/01/2016
 * @author andrea-muti
 */

/**
 *  HOW IT WORKS :   A quanto pare non funziona se ciò lo fa lo stesso programma (ma se uno apre/chiude ssh e l'altro fa jmx si)
 *  	
 *  	1) Create the Tunnel Manager :
 *      	- SSHTunnelManager sshmanager = new SSHTunnelManager(remote_user, remote_pass, remote_addr, remote_jmx);
 *      
 *      2) Open the SSH Tunnel : 
 *       	- boolean opened = sshmanager.open_tunnel();
 * 
 *      3) DO YOUR STUFF THAT USES THE TUNNEL [ i.e. le chiamate con connessione JMX che altrimenti non funzionano ]
 *       
 *      4) Close the SSH Tunnel : 
 *      	- sshmanager.close_tunnel();
 * 
 */


public class SSHTunnelManager {
	
	private int pid;
	private Process process;
	
	private String user;
	private String password;
	private String remote_address;
	private String port_number;

	
	public SSHTunnelManager(String user, String pass, String remote, String port){
		this.user			= user;
		this.password		= pass;
		this.remote_address = remote;
		this.port_number	= port;
	}
	
	
	public boolean open_tunnel(){
		
		boolean result = false;
		
		String open_command ="sshpass -p "+this.password+" ssh -D "+this.port_number+" "+this.user+"@"+this.remote_address+" ";
	
		try {
			this.process = Runtime.getRuntime().exec(open_command);
			
			Field f = this.process.getClass().getDeclaredField("pid");
			f.setAccessible(true);
			this.pid = f.getInt(this.process) ;
			f.setAccessible(false);
			System.out.println(" - Tunnel Process PID : "+ this.pid);
			
			Thread.sleep(1000);
						
			// BISOGNA ASPETTARE QUALCHE SECONDO CHE IL COMANDO VENGA ESEGUITO COMPLETAMENTE E VENGA QUINDI
			// STABILITO IL TUNNEL SSH
			// SE NON ASPETTO E RITORNO SUBITO, HO ERRORI PERCHE' CHI PROVA A USARE IL TUNNEL POTREBBE TROVARLO NON ANCORA PRONTO
		    // valutare bene il tempo da aspettare
			       
			result = true;
			
		} catch (Exception e) { System.out.println(e.getMessage()+"\n");result = false; }
         
        return result;
	}
	
	public void close_tunnel(){
		this.process.destroyForcibly();
	}
	
    public static void main( String[] args ){
    	
    	String remote_user = "muti";
    	String remote_pass = "mUt1";
    	String remote_addr = "vm0";
    	String remote_jmx  = "7199";
    	
        SSHTunnelManager sshmanager = new SSHTunnelManager(remote_user, remote_pass, remote_addr, remote_jmx);
        boolean opened = sshmanager.open_tunnel();
        System.out.println("opened? "+opened);
        
        if(!opened){
        	System.err.println("ERROR : failed to open the SSH Tunnel");
        	System.exit(-1);
        }
        
        System.out.println("press any key to close the tunnel");
        try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        sshmanager.close_tunnel();
        System.out.println("ssh tunnel closed");
        System.exit(0);
    }
}

