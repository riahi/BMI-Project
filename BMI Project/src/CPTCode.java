import java.util.ArrayList;

// 2011-07-07
public class CPTCode {
	
	private ArrayList<Double> BMIList;
	private String CPTCode;
	private String CPTComment;
	private String surgeon;
	
	public CPTCode(String CPTCode) {
		BMIList = new ArrayList<Double>();
		this.CPTCode = CPTCode;
		CPTComment = new String();
		surgeon = new String();
	}
	
	public CPTCode(String CPTCode, String CPTComment) {
		BMIList = new ArrayList<Double>();
		this.CPTCode = CPTCode;
		this.CPTComment = CPTComment;
		surgeon = new String();
	}
	
	public CPTCode(String CPTCode, String CPTComment, ArrayList<Double> BMIList) {
		this.BMIList = BMIList;
		this.CPTCode = CPTCode;
		this.CPTComment = new String();
		surgeon = new String();
	}
	
	public CPTCode(String CPTCode, String CPTComment, String surgeon) {
		BMIList = new ArrayList<Double>();
		this.CPTCode = CPTCode;
		this.CPTComment = CPTComment;
		this.surgeon = surgeon;
	}

	/**
	 * @return double average BMI of the elements stored in this CPTCode
	 */
	public double getAverageBMI() {
		double sum = 0.0;
		
		for(int i = 0; i < BMIList.size(); i++) {
			sum += BMIList.get(i);
		}
		
		return (sum / (double) BMIList.size());
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return CPTCode + "~" + CPTComment + "~" + getAverageBMI() + "~" + BMIList.size() + "~" + surgeon;
	}

	public void addBMI(double BMI) {
		BMIList.add(new Double(BMI));
	}
	
	/**
	 * @return the bMIList
	 */
	public ArrayList<Double> getBMIList() {
		return BMIList;
	}

	/**
	 * @param bMIList the bMIList to set
	 */
	public void setBMIList(ArrayList<Double> BMIList) {
		this.BMIList = BMIList;
	}

	/**
	 * @return the cPTCode
	 */
	public String getCPTCode() {
		return CPTCode;
	}

	/**
	 * @param cPTCode the cPTCode to set
	 */
	public void setCPTCode(String cPTCode) {
		CPTCode = cPTCode;
	}

	/**
	 * @return the cPTComment
	 */
	public String getCPTComment() {
		return CPTComment;
	}

	/**
	 * @param cPTComment the cPTComment to set
	 */
	public void setCPTComment(String cPTComment) {
		CPTComment = cPTComment;
	}

	/**
	 * @return the surgeon
	 */
	public String getSurgeon() {
		return surgeon;
	}

	/**
	 * @param surgeon the surgeon to set
	 */
	public void setSurgeon(String surgeon) {
		this.surgeon = surgeon;
	}
	
	
}
