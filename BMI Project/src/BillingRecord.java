import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Class that represents each entry in the E-Ticket Billing Database as provided
 * to me by Doris Cepeda. This and the suite of MedicalRecord, AnesthesiaRecord,
 * BillingRecord, and ModifierRecord will need to be refactored at some point,
 * but not today.
 * 
 * @author Shahein Tajmir
 * @version 2011-07-15
 */

public class BillingRecord {
	public static final int DIAGNOSIS_NUMBER = 5;
	public static final int PROCEDURE_NUMBER = 22;
	private int MRN;
	private String patientName;
	private int Age;
	private String gender;
	private String surgeon;
	private Calendar DOS;
	private ArrayList<ICD9Code> diagnosis;
	private ArrayList<CPTCode> procedure;

	/**
	 * Method constructor that initializes all variables. This is the main
	 * constructor used when loading from a CSV file. It may be prudent to move
	 * the string parsing into this class, rather than have it sit in the main
	 * method in BMIStats as it is specific to this class.
	 * 
	 * @param MRN
	 *            integer representation of the MRN
	 * @param patientName
	 *            String in format of "LASTNAME,FIRSTNAME" as per BIDMC
	 *            formatting
	 * @param Age
	 *            integer patient age at service
	 * @param gender
	 *            String gender as "M" or "F"
	 * @param surgeon
	 *            String in format of "LastName, FirstName MD" as per BIDMC
	 *            formatting
	 * @param DOS
	 *            Calendar object used to represent that date of service
	 * @param Diagnosis
	 *            ArrayList of ICD9Code used to represent the diagnoses assigned
	 *            to this patient.
	 * @param Procedure
	 *            ArrayList of CPTCode representing the procedures assigned to
	 *            this patient.
	 */
	public BillingRecord(int MRN, String patientName, int age, String gender,
			String surgeon, Calendar DOS, ArrayList<ICD9Code> diagnosis,
			ArrayList<CPTCode> procedure) {
		this.setMRN(MRN);
		this.setPatientName(patientName);
		this.setAge(age);
		this.setGender(gender);
		this.setSurgeon(surgeon);
		this.setDOS(DOS);
		this.setMRN(MRN);
		this.setDiagnosis(diagnosis);
		this.setProcedure(procedure);
	}

	public String toString() {
		String temp = "";
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

		temp += MRN + "~" + dateFormatter.format(DOS.getTime()) + "~"
				+ patientName + "~" + Age + "~" + gender + "~" + surgeon;

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

	public static void main(String[] args) {
		Calendar DOS = Calendar.getInstance();
		DOS.set(1999, 0, 1);

		ArrayList<ICD9Code> Diagnosis = new ArrayList<ICD9Code>();
		Diagnosis.add(new ICD9Code("11223", "Awesome pubic bone"));
		//Diagnosis.add(new ICD9Code("11223", "Awesome pubic bone"));
		//Diagnosis.add(new ICD9Code("11223", "Awesome pubic bone"));

		ArrayList<CPTCode> Procedure = new ArrayList<CPTCode>();
		Procedure.add(new CPTCode("3456", "Evaluation for awesome pubic bone"));

		BillingRecord bRecord = new BillingRecord(1234567, "Shahein Tajmir",
				22, "Male", "Arun Ramappa", DOS, Diagnosis, Procedure);
		System.out.println(bRecord.toString());
		System.out.println(bRecord.createKey());
	}

	/**
	 * @return the an int containing the Medical Record Number
	 */
	public int getMRN() {
		return MRN;
	}

	/**
	 * @param int MRN - to set patient MRN
	 */
	public void setMRN(int mRN) {
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
	 * @return the age
	 */
	public int getAge() {
		return Age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age) {
		Age = age;
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
}
