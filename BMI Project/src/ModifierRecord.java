import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Class that represents each entry in the Dec-2009 to now Billing Database as
 * provided to me by Karen Murray. This and the suite of MedicalRecord,
 * AnesthesiaRecord, BillingRecord, and ModifierRecord will need to be
 * refactored at some point, but not today.
 * 
 * @author Shahein Tajmir
 * @version 2011-07-15
 */
public class ModifierRecord {
	public static final int DIAGNOSIS_NUMBER = 4;
	public static final int PROCEDURE_NUMBER = 1;

	private String MRN;
	private String patientName;
	private String gender;
	private String surgeon;
	private Calendar DOS;
	private Calendar DOB;
	private ArrayList<ICD9Code> diagnosis;
	private ArrayList<CPTCode> procedure;
	private String mod1;
	private String mod2;
	private int charge;

	/**
	 * @param surgeon
	 * @param DOS
	 * @param MRN
	 * @param patientName
	 * @param gender
	 * @param DOB
	 * @param Procedure
	 * @param Diagnosis
	 * @param mod1
	 * @param mod2
	 * @param charge
	 */
	public ModifierRecord(String surgeon, Calendar DOS, String MRN,
			String patientName, String gender, Calendar DOB,
			ArrayList<CPTCode> procedure, ArrayList<ICD9Code> diagnosis,
			String mod1, String mod2, int charge) {
		this.setMRN(MRN);
		this.setPatientName(patientName);
		this.setGender(gender);
		this.setSurgeon(surgeon);
		this.setDOS(DOS);
		this.setDOB(DOB);
		this.setDiagnosis(diagnosis);
		this.setProcedure(procedure);
		this.setMod1(mod1);
		this.setMod2(mod2);
		this.setCharge(charge);
	}

	public String toString() {
		String temp = new String("");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

		temp += surgeon + "~" + dateFormatter.format(DOS.getTime()) + "~" + MRN
				+ "~" + patientName + "~" + gender + "~"
				+ dateFormatter.format(DOB.getTime());

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

		temp += "~" + mod1 + "~" + mod2 + "~" + charge;
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

	public static void main(String[] args) {
		Calendar DOS = Calendar.getInstance();
		DOS.set(1999, 0, 1);
		Calendar DOB = Calendar.getInstance();
		DOB.set(1988, 7, 3);

		ArrayList<ICD9Code> Diagnosis = new ArrayList<ICD9Code>();
		Diagnosis.add(new ICD9Code("11223", "Awesome pubic bone"));
		//Diagnosis.add(new ICD9Code("11223", "Awesome pubic bone"));
		//Diagnosis.add(new ICD9Code("11223", "Awesome pubic bone"));

		ArrayList<CPTCode> Procedure = new ArrayList<CPTCode>();
		Procedure.add(new CPTCode("3456", "Evaluation for awesome pubic bone"));

		ModifierRecord mRecord = null;
		mRecord = new ModifierRecord("Ramappa, Arun MD", DOS, "1234567",
				"Tajmir,Shahein", "M", DOB, Procedure, Diagnosis, "22", "23",
				500);
		System.out.println(mRecord);
	}

	/**
	 * @return the an int containing the Medical Record Number
	 */
	public String getMRN() {
		return MRN;
	}

	/**
	 * @param int MRN - to set patient MRN
	 */
	public void setMRN(String mRN) {
		MRN = mRN;
	}

	/**
	 * @return String containing Patient's Name
	 */
	public String getPatientName() {
		return patientName;
	}

	/**
	 * @param patientName
	 *            the patientName to set
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
	 * @param gender
	 *            the gender to set
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
	 * @param surgeon
	 *            the surgeon to set
	 */
	public void setSurgeon(String surgeon) {
		this.surgeon = surgeon;
	}

	/**
	 * @return Calendar DOS - Date of Surgery
	 */
	public Calendar getDOS() {
		return DOS;
	}

	/**
	 * @param dOS
	 *            the dOS to set
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

	/**
	 * @return the dOB
	 */
	public Calendar getDOB() {
		return DOB;
	}

	/**
	 * @param dOB
	 *            the dOB to set
	 */
	public void setDOB(Calendar dOB) {
		DOB = dOB;
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
}
