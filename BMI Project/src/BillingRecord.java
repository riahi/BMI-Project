// 2010-07-05 
// Shahein Tajmir

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BillingRecord {
	private int MRN;
	private String patientName;
	private int Age;
	private String gender;
	private String surgeon;
	private Calendar DOS;
	private String[][] Diagnosis;
	private String[][] Procedure;
	
	public BillingRecord(int MRN, String patientName, int Age, String gender, String surgeon, Calendar DOS, String[][] Diagnosis, String[][] Procedure) {
		this.setMRN(MRN);
		this.setPatientName(patientName);
		this.setAge(Age);
		this.setGender(gender);
		this.setSurgeon(surgeon);
		this.setDOS(DOS);
		this.setGender(gender);
		this.setMRN(MRN);
		this.Diagnosis = Diagnosis;
		this.Procedure = Procedure;
	}
	
	public String toString() {
		String temp = new String("");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
		
		temp += MRN + " " + dateFormatter.format(DOS.getTime()) + " " + patientName + " " + Age + " " + gender + " " + surgeon;
		
		// Diagnosis fields
				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < 2; j++) {
						// Escape the comments
						if(j % 2 == 1)
							temp += "~" + Diagnosis[i][j];
						else
							temp += "~" + Diagnosis[i][j];
					}
				}
				
				// Procedure fields
						for (int i = 0; i < 22; i++) {
							for (int j = 0; j < 2; j++) {
								// Escape the comments
								if(j % 2 == 1)
									temp += "~" + Procedure[i][j];
								else
									temp += "~" + Procedure[i][j];
							}
						}
		
		return temp;
	}
	
	public static void main(String[] args) {
		Calendar DOS = Calendar.getInstance();
		DOS.set(1999, 0, 1);
		
		String[][] Diagnosis = new String[5][2];
		Diagnosis[0][0] = "11223";
		Diagnosis[0][1] = "Awesome pubic bone";
		
		String[][] Procedure = new String[22][2];
		Procedure[0][0] = "3456";
		Procedure[0][1] = "Evaluation for awesome pubic bone";
		
		
		BillingRecord aRecord = new BillingRecord(1234567, "Shahein Tajmir", 22, "Male", "Arun Ramappa", DOS, Diagnosis, Procedure);
		System.out.println(aRecord.toString());
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
	 * @param patientName the patientName to set
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
	 * @param age the age to set
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
	 * @return Calendar DOS - Date of Surgery
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
	public String[][] getDiagnosis() {
		return Diagnosis;
	}

	/**
	 * @param diagnosis the diagnosis to set
	 */
	public void setDiagnosis(String[][] diagnosis) {
		Diagnosis = diagnosis;
	}

	/**
	 * @return the procedure
	 */
	public String[][] getProcedure() {
		return Procedure;
	}

	/**
	 * @param procedure the procedure to set
	 */
	public void setProcedure(String[][] procedure) {
		Procedure = procedure;
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
}
