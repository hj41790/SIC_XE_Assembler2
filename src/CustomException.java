
public class CustomException extends Exception {

	int errCode;
	
	CustomException(int a){
		super(errMsg[a]);
		errCode = a;
	}
	
	public int getErrorCode(){
		return errCode;
	}
	
	
	static final int NULL_POINTER_EXCEPTION		= 1;
	static final int WRONG_FORMAT				= 2;
	static final int OPERATOR_UNDEFINED			= 3;
	static final int OPERAND_SHORTAGE			= 4;
	static final int OPERAND_EXCEED				= 5;
	static final int OPERAND_ERR				= 6;
	static final int SYMBOL_WRONG_NAME			= 7;
	static final int SYMBOL_UNDEFINED			= 8;
	static final int SYMBOL_DUPLICATION			= 9;
	static final int REGISTER_UNDEFINED			= 10;
	static final int SYMBOL_NO_REFERENCE		= 11;
	
	private final static String[] errMsg = {
			
					"",
					"1. null pointer exception",
					"2. Wrong foramt",
					"3. undefined operator",
					"4. insufficient operand",
					"5. too much operand",
					"6. operand error",
					"7. wrong format symbol name",
					"8. undefined symbol",
					"9. symbol name duplication",
					"10. undefined register",
					"11. no reference"
	};
	
}