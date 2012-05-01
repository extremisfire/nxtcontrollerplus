package nxtcontroller.program.btmessages.commands;

public class DirectCommandOutput extends DirectCommand{

	/**
	 * port where motor is connected
	 * @param portNumber <0,2>, on NXT: A:0,B:1,C:1
	 * @throws UnsupportedOperationException
	 */
	public void setOutputPort(byte portNumber) throws UnsupportedOperationException{
		if(portNumber<0 || portNumber > 2)
			throw new UnsupportedOperationException("only <0-2> ports are legal");
		super.command[2] = portNumber;
		super.refreshCommand();
	}
	
	protected DirectCommandOutput(byte commandLength, byte commandType) {
		super(commandLength, commandType);
		// TODO Auto-generated constructor stub
	}

	public String toString(){
		String temp = super.toString()+"\n";
		temp += "Input port: "+Integer.toString(command[2])+"\n";
		return temp;
	}
}
