/**
 * Class that represents the basics of many medical codes used in billing
 * systems. Most codes have a code and a comment associated.
 *  
 * @author Shahein Tajmir
 * @version 2011-07-15
 */
public class MedicalCode {
	private String code;
	private String comment;

	/**
	 * Class constructor specifying the code value and comment
	 * 
	 * @param code
	 *            the string representation of the medical code
	 * @param comment
	 *            the string that explains a code
	 */
	public MedicalCode(String code, String comment) {
		setCode(code);
		setComment(comment);
	}

	public String toString() {
		return code + "~" + comment;
	}
	
	public boolean equals(CPTCode e) {
		boolean Code;
		boolean Comment;
		
		Code = this.code.equals(e.getCode());
		Comment = this.comment.equals(e.getComment());
		return Code && Comment;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

}
