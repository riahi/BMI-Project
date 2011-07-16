import java.util.ArrayList;

/**
 * Class extends MedicalCode to include the many transformations needed when
 * working with CPTCodes in particular.
 * 
 * @author Shahein Tajmir
 * @version 2011-07-15
 * 
 */

public class CPTCode extends MedicalCode {

	public static final int CHARGE_UNASSIGNED = -999;
	// BMIList is used when trying to see all the BMIs that a given CPTcode has
	// been assigned to
	private ArrayList<Double> BMIList;
	// surgeon is used when classifying CPTCodes by surgeon or service
	private String surgeon;
	// mod1 and mod2 are used when seeing what modifiers have been applied to
	// each CPTCode
	private String mod1;
	private String mod2;
	private int charge = CHARGE_UNASSIGNED;

	public CPTCode(String code, String comment) {
		super(code, comment);
		BMIList = new ArrayList<Double>();
		surgeon = new String();
	}

	public CPTCode(String code, String comment, ArrayList<Double> BMIList) {
		super(code, comment);
		this.BMIList = BMIList;
		surgeon = new String();
	}

	public CPTCode(String code, String comment, String surgeon) {
		super(code, comment);
		BMIList = new ArrayList<Double>();
		this.surgeon = surgeon;
	}

	/**
	 * Calculates and returns the average BMI of the patients stored in this CPTCode
	 * @return average BMI of the elements stored in this CPTCode
	 */
	public double getAverageBMI() {
		double sum = 0.0;
		for (int i = 0; i < BMIList.size(); i++) {
			sum += BMIList.get(i);
		}
		return (sum / (double) BMIList.size());
	}

	public String getBMIString() {
		return getCode() + "~" + getComment() + "~" + getAverageBMI() + "~"
				+ BMIList.size() + "~" + surgeon;
	}


	/* (non-Javadoc)
	 * @see MedicalCode#toString()
	 */
	public String toString() {
		String temp = getCode() + "~" + getComment();
		if(getMod1() != null) 
			temp += "~" + getMod1();
		if(getMod2() != null)
			temp += "~" + getMod2(); 
		if(getCharge() != CHARGE_UNASSIGNED)
			temp += "~" + getCharge();
		return temp;
	}

	/**
	 * @return the mod1
	 */
	public String getMod1() {
		return mod1;
	}

	/**
	 * @param mod1
	 *            the mod1 to set
	 */
	public void setMod1(String mod1) {
		this.mod1 = mod1;
	}

	/**
	 * @return the mod2
	 */
	public String getMod2() {
		return mod2;
	}

	/**
	 * @param mod2
	 *            the mod2 to set
	 */
	public void setMod2(String mod2) {
		this.mod2 = mod2;
	}

	/**
	 * @return the charge
	 */
	public int getCharge() {
		return charge;
	}

	/**
	 * @param charge
	 *            the charge to set
	 */
	public void setCharge(int charge) {
		this.charge = charge;
	}

	/**
	 * @param BMI
	 */
	public void addBMI(double BMI) {
		BMIList.add(new Double(BMI));
	}

	/**
	 * @return the BMIList
	 */
	public ArrayList<Double> getBMIList() {
		return BMIList;
	}

	/**
	 * @param BMIList
	 *            the BMIList to set
	 */
	public void setBMIList(ArrayList<Double> BMIList) {
		this.BMIList = BMIList;
	}

	/**
	 * @return the surgeon
	 */
	public String getSurgeon() {
		return surgeon;
	}

	/**
	 * @param surgeon
	 *            the surgeon to set
	 */
	public void setSurgeon(String surgeon) {
		this.surgeon = surgeon;
	}

}
