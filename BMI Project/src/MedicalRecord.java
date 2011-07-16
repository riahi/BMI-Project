// 2011-07-14

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class MedicalRecord {
	public static final int DIAGNOSIS_NUMBER = 5;
	public static final int PROCEDURE_NUMBER = 22;
	
	private int MRN;
	private int Age;
	private double weight;
	private double height;
	private double BMI;
	private String patientName;
	private String gender;
	private String surgeon;
	private ArrayList<ICD9Code> diagnosis;
	private ArrayList<CPTCode> procedure;
	private Calendar DOB;
	private Calendar DOS;
	private String mod1;
	private String mod2;
	private int charge;
	
	public MedicalRecord(AnesthesiaRecord a, BillingRecord b) {
		int MRN1, MRN2;
		String gender1, gender2;
		String surgeon1, surgeon2;
		
		this.DOB = a.getDOB();
		this.DOS = a.getDOS();
		this.weight = a.getWeight();
		this.height = a.getHeight();
		surgeon1 = a.getSurgeon();
		this.BMI = a.getBMI();
		gender1 = a.getGender();
		MRN1 = a.getMRN();
		
		MRN2 = b.getMRN();
		this.patientName = b.getPatientName();
		this.Age = b.getAge();
		gender2 = b.getGender();
		surgeon2 = b.getSurgeon();
		this.diagnosis = b.getDiagnosis();
		this.procedure = b.getProcedure();
		
		if (MRN1 == MRN2)
			this.MRN = MRN1;
		else {
			System.out.println("MRN does not match for " + patientName + ". Defaulting to Anesthesia's value");
			this.MRN = MRN1;
		}
		
		if (surgeon1.equals(surgeon2))
			this.surgeon = surgeon1;
		else {
			System.out.println("Surgeon does not match for " + patientName + ". Defaulting to Anesthesia's value");
			this.surgeon = surgeon1;
		}
		
		if (gender1.equals(gender2))
			this.gender = gender1;
		else {
			System.out.println("Gender does not match for " + patientName + ". Defaulting to Anesthesia's value");
			this.gender = gender1;
		}	
		
		mod1 = null;
		mod2 = null;
	}
	
	/*public MedicalRecord(AnesthesiaRecord a, BillingRecord b, ModifierRecord c) {
		int MRN1, MRN2;
		String gender1, gender2;
		String surgeon1, surgeon2;
		
		this.DOB = a.getDOB();
		this.DOS = a.getDOS();
		this.weight = a.getWeight();
		this.height = a.getHeight();
		surgeon1 = a.getSurgeon();
		this.BMI = a.getBMI();
		gender1 = a.getGender();
		MRN1 = a.getMRN();
		
		MRN2 = b.getMRN();
		this.patientName = b.getPatientName();
		this.Age = b.getAge();
		gender2 = b.getGender();
		surgeon2 = b.getSurgeon();
		this.Diagnosis = b.getDiagnosis();
		this.Procedure = b.getProcedure();
		
		if (MRN1 == MRN2)
			this.MRN = MRN1;
		else {
			System.out.println("MRN does not match for " + patientName + ". Defaulting to Anesthesia's value");
			this.MRN = MRN1;
		}
		
		if (surgeon1.equals(surgeon2))
			this.surgeon = surgeon1;
		else {
			System.out.println("Surgeon does not match for " + patientName + ". Defaulting to Anesthesia's value");
			this.surgeon = surgeon1;
		}
		
		if (gender1.equals(gender2))
			this.gender = gender1;
		else {
			System.out.println("Gender does not match for " + patientName + ". Defaulting to Anesthesia's value");
			this.gender = gender1;
		}
		
		this.mod1 = c.getMod1();
		this.mod2 = c.getMod2();
		this.charge = c.getCharge();
	}
	*/
	
	public String toString() {
		String temp = new String("");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
		
		temp = MRN + "~" + dateFormatter.format(DOB.getTime()) + "~" + dateFormatter.format(DOS.getTime()) + "~" + BMI + "~"; 
		temp += weight + "~" + height + "~" + surgeon + "~" + gender + "~";
		temp += patientName + "~" + Age;
		
		// Diagnosis fields
		// If diagnosis has less than the diagnosis count specified by
		// DIAGNOSIS_NUMBER, pad it out to the proper amount
		if (diagnosis.size() < DIAGNOSIS_NUMBER) {
			int difference = DIAGNOSIS_NUMBER - diagnosis.size();
			for (int i = 0; i < diagnosis.size(); i++) {
				temp += "~" + diagnosis.get(i).toString();
			}
			for (int i = 0; i < difference; i++) {
				temp += "~" + "" + "~" + "";
			}
		} else {
			for (int i = 0; i < diagnosis.size(); i++) {
				temp += "~" + diagnosis.get(i).toString();
			}
		}

		// Procedure Fields
		// If procedure has less than the procedure count specific by
		// PROCEDURE_NUMBER, pad it out to the proper amount
		if (procedure.size() < PROCEDURE_NUMBER) {
			int difference = PROCEDURE_NUMBER - procedure.size();
			for (int i = 0; i < procedure.size(); i++) {
				temp += "~" + procedure.get(i).toString();
			}
			for (int i = 0; i < difference; i++) {
				temp += "~" + "" + "~" + "";
			}
		} else {
			for (int i = 0; i < procedure.size(); i++) {
				temp += "~" + procedure.get(i).toString();
			}
		}

		
		return temp;
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

	public static void main(String args[]) {
		Calendar DOB = Calendar.getInstance();
		DOB.set(1988, 7, 3);
		Calendar DOS = Calendar.getInstance();
		DOS.set(1999, 0, 1);
		AnesthesiaRecord aRecord = new AnesthesiaRecord(DOB, DOS, 175.0, 68.1, "Arun Ramappa", 25.6, "Male", 1234567);

		ArrayList<ICD9Code> Diagnosis = new ArrayList<ICD9Code>();
		Diagnosis.add(new ICD9Code("11223", "Awesome pubic bone"));
		//Diagnosis.add(new ICD9Code("11223", "Awesome pubic bone"));
		//Diagnosis.add(new ICD9Code("11223", "Awesome pubic bone"));

		ArrayList<CPTCode> Procedure = new ArrayList<CPTCode>();
		Procedure.add(new CPTCode("3456", "Evaluation for awesome pubic bone"));

		BillingRecord bRecord = new BillingRecord(1234567, "Shahein Tajmir",
				22, "Male", "Arun Ramappa", DOS, Diagnosis, Procedure);
		
		MedicalRecord mRecord = new MedicalRecord(aRecord, bRecord);
		
		System.out.println(mRecord);
		
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

	/**
	 * @return the age
	 */
	public int getAge() {
		return Age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		Age = age;
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
	 * @return the patientName
	 */
	public String getPatientName() {
		return patientName;
	}

	/**
	 * @param patientName the patientName to set
	 */
	public void setPatientName(String patientName) {
		this.patientName = patientName;
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
	 * @return the diagnosis
	 */
	public ArrayList<ICD9Code> getDiagnosis() {
		return diagnosis;
	}

	/**
	 * @param diagnosis
	 *            the diagnosis to set
	 */
	public void setDiagnosis(ArrayList<ICD9Code> diagnosis) {
		this.diagnosis = diagnosis;
	}

	/**
	 * @return the procedure
	 */
	public ArrayList<CPTCode> getProcedure() {
		return procedure;
	}

	/**
	 * @param procedure
	 *            the procedure to set
	 */
	public void setProcedure(ArrayList<CPTCode> procedure) {
		this.procedure = procedure;
	}
}
