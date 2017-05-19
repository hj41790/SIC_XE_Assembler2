
public class CustomException extends Exception {

	int errCode;
	
	CustomException(int a){
		super(errMsg[Math.abs(a)]);
		errCode = a;
	}
	
	public int getErrorCode(){
		return errCode;
	}
	
	
	static int DIRECTIVE_BYTE_CHARACTOR = -1;
	static int DIRECTIVE_BYTE_QUOTATION = -2;
	static int OPERATOR_UNDEFINED		= -3;
	static int OPERAND_NOT_INTEGER		= -4;
	static int SYMBOL_NAME_DUPLICATION	= -5;
	static int NO_OPERAND				= -6;
	static int OPERAND_TOO_MUCH			= -7;
	static int SYMBOL_NAME_NULL			= -8;
	static int TOKEN_PARSING_ERR		= -9;
	static int NULL_POINTER_EXCEPTION	= -11;
	static int PARAMETER_WORNG_FORMAT	= -12;
	
	private static String[] errMsg = {
			
					"",
					"1. Wrong Starting Charactor for BYTE directive",
					"2. Not Matching about quotation mark",
					"3. Undefined Operator",
					"4. Operand is not integer",
					"5. Symbol name duplication",
					"6. There is No Operand",
					"7. too much operands",
					"8. symbol name parameter is null",
					"9. token parsing error",
					"11. null pointer exception",
					"12. parameter wrong format"
	};
	
}