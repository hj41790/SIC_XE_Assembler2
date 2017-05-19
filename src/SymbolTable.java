import java.util.ArrayList;

public class SymbolTable {

	// singleton class
	
	private static SymbolTable table = null;
	
	private ArrayList<Symbol> array = null;
	
	
	public static SymbolTable getInstance(){
		
		if(table == null){
			table = new SymbolTable();
		}
		
		return table;
	}
	
	
	private SymbolTable(){
		array = new ArrayList<Symbol>();
	}
	
	public Symbol addSymbol(String name, Token t){
		
		if(name == null || t == null) return null;
		
		Symbol sym = new Symbol(name, t.getSection(), t.getAddr());
		array.add(sym);
		
		return sym;
	}
	
	public Symbol search_localSymbol(String n, int section) throws CustomException{
		
		if(n == null) throw new CustomException(CustomException.SYMBOL_NAME_NULL);
		
		String name;
		if(n.charAt(0)=='@') name = n.substring(1);
		else name = n.substring(0);
		
		for(Symbol s : array){
			if(s.compareName(name) && s.getSection()==section)
				return s;
		}
		
		return null;
	}
	
	public Symbol search_globalSymbol(String n) throws CustomException{
		
		if(n == null) throw new CustomException(CustomException.SYMBOL_NAME_NULL);
		
		String name;
		if(n.charAt(0)=='@') name = n.substring(1);
		else name = n.substring(0);
		
		for(Symbol s : array){
			if(s.compareName(name) && s.isEXTDEF())
				return s;
		}
		
		return null;
	}
	
}
