/**
 * Class that extends the MedicalCode class for the specific cases of ICD9
 * codes.
 * 
 * @author Shahein Tajmir
 * @version 2011-07-15
 */
public class ICD9Code extends MedicalCode {

	/**
	 * Class Constructor. Requires values for the ICD9 code and comment
	 * @param code String value of the code
	 * @param comment String value of code's description
	 */
	public ICD9Code(String code, String comment) {
		super(code, comment);
	}

}
