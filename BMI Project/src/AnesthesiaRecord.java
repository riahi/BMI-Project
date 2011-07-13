// 2010-07-05 
// Shahein Tajmir

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AnesthesiaRecord {
	private Calendar DOB;
	private Calendar DOS;
	private double weight;
	private double height;
	private String surgeon;
	private double BMI;
	private String gender;
	private int MRN;
	
	public AnesthesiaRecord(Calendar DOB, Calendar DOS, double weight, double height, String surgeon, double BMI, String gender, int MRN) {
		this.setDOB(DOB);
		this.setDOS(DOS);
		this.setWeight(weight);
		this.setHeight(height);
		this.setSurgeon(surgeon);
		this.setBMI(BMI);
		this.setGender(gender);
		this.setMRN(MRN);
	}
	
	public String toString() {
		String temp = new String("");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
		
		temp += MRN + " " + dateFormatter.format(DOB.getTime()) + " " + dateFormatter.format(DOS.getTime()) + " " + weight + " " + height + " " + surgeon + " " + BMI + " " + gender;
		
		return temp;
	}

	/**
	 * @return the dOB
	 */
	public Calendar getDOB() {
		return DOB;
	}

	/**
	 * @param dOB the dOB to set
	 */
	public void setDOB(Calendar dOB) {
		DOB = dOB;
	}

	/**
	 * @return the dOS
	 */
	public Calendar getDOS() {
		return DOS;
	}

	/**
	 * @param dOS the dOS to set
	 */
	public void setDOS(Calendar dOS) {
		DOS = dOS;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
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

	/**
	 * @return the bMI
	 */
	public double getBMI() {
		return BMI;
	}

	/**
	 * @param bMI the bMI to set
	 */
	public void setBMI(double bMI) {
		BMI = bMI;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the mRN
	 */
	public int getMRN() {
		return MRN;
	}

	/**
	 * @param mRN the mRN to set
	 */
	public void setMRN(int mRN) {
		MRN = mRN;
	}
	
	public String createKey() {
		String key;
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

		key = "" + dateFormatter.format(this.getDOS().getTime());
		key = key + "~" + this.getSurgeon();
		key = key + "~" + this.getMRN();
		key = key + "~" + this.getGender();	
		return key;	
	}
	
	public static void main(String[] args) {
		Calendar DOB = Calendar.getInstance();
		DOB.set(1988, 7, 3);
		Calendar DOS = Calendar.getInstance();
		DOS.set(1999, 0, 1);
		AnesthesiaRecord aRecord = new AnesthesiaRecord(DOB, DOS, 175.0, 68.1, "Arun Ramappa", 25.6, "Male", 1234567);
		System.out.println(aRecord.toString());
	}
}
