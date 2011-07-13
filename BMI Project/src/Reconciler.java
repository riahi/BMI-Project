import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class Reconciler {

	// Takes a CSV from Billing Database and imports to hashtable
	// Uses CSVReader class to slurp up each line in the CSV file, putting each
	// line into a queue (LinkedList), then popping off each
	// element as needed. The old array implementation of this was terrible.
	public static void importToConcurrentHashMap(String filename,
			ConcurrentHashMap<String, BillingRecord> table) throws IOException {
		BillingRecord bRecord;

		try {
			CSVReader reader = new CSVReader(new FileReader(
					"/Users/shahein/Desktop/" + filename));

			String[] line;
			String[] tempDate;
			LinkedList<String> q = new LinkedList<String>();

			// Variables for each BillingRecord
			int MRN;
			String patientName;
			int Age;
			String gender;
			String surgeon;
			Calendar DOS;
			String[][] Diagnosis = new String[5][2];
			String[][] Procedure = new String[22][2];

			System.out.println("BillingTable Construction");

			// For each line, convert the csv into a BillingRecord object and
			// add it to the passed cHashMap.
			while ((line = reader.readNext()) != null) {
				// Put the array of strings into a queue
				for (int i = 0; i < line.length; i++) {
					q.add(line[i]);
				}

				MRN = Integer.parseInt(q.poll());
				patientName = q.poll();
				Age = Integer.parseInt(q.poll());
				gender = q.poll();
				surgeon = q.poll();
				tempDate = q.poll().split("/");
				DOS = Calendar.getInstance();
				// Janky changes for DOS because of the time-range.
				DOS.set(Integer.parseInt(tempDate[2]) + 2000,
						Integer.parseInt(tempDate[0]) - 1,
						Integer.parseInt(tempDate[1]));

				// Diagnosis fields
				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < 2; j++) {
						Diagnosis[i][j] = q.poll();
					}
				}

				// Procedure fields
				for (int i = 0; i < 22; i++) {
					for (int j = 0; j < 2; j++) {
						Procedure[i][j] = q.poll();
					}
				}

				bRecord = new BillingRecord(MRN, patientName, Age, gender,
						surgeon, DOS, Diagnosis, Procedure);
				// Debug
				System.out.println(bRecord);
				table.put(bRecord.createKey(), bRecord);
				
				//System.out.println("Immediately from table");
				//System.out.println(table.get(new Integer(MRN)));
				
				bRecord = null;
				Diagnosis = new String[5][2];
				Procedure = new String[22][2];
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void importToLinkedList(String filename,
			LinkedList<AnesthesiaRecord> aList) throws IOException {
		AnesthesiaRecord aRecord;

		try {
			CSVReader reader = new CSVReader(new FileReader(
					"/Users/shahein/Desktop/" + filename));

			String[] line;
			String[] tempDate;
			LinkedList<String> q = new LinkedList<String>();

			// Variables for each AnesthesiaRecord
			Calendar DOB;
			Calendar DOS;
			double weight;
			double height;
			String surgeon;
			double BMI;
			String gender;
			int MRN;

			System.out.println("AnesthesiaTable Construction");

			// For each line, convert the csv into a BillingRecord object and
			// add it to the passed cHashMap.
			while ((line = reader.readNext()) != null) {
				// Put the array of strings into a queue
				for (int i = 0; i < line.length; i++) {
					q.add(line[i]);
				}

				tempDate = q.poll().split("/");
				DOB = Calendar.getInstance();
				DOB.set(Integer.parseInt(tempDate[2]) + 1900,
						Integer.parseInt(tempDate[0]) - 1,
						Integer.parseInt(tempDate[1]));

				tempDate = q.poll().split("/");
				DOS = Calendar.getInstance();
				if(tempDate.length == 3)
					DOS.set(Integer.parseInt(tempDate[2]) + 2000,
							Integer.parseInt(tempDate[0]) - 1,
							Integer.parseInt(tempDate[1]));
				else
					DOS.set(0, 0, 0);

				String weightStr = q.poll();
				if(weightStr.equals(""))
					weight = 0;
				else
					weight = Double.parseDouble(weightStr);
				
				String heightStr = q.poll();
				if(heightStr.equals(""))
					height = 0;
				else
					height = Double.parseDouble(heightStr);
				
				surgeon = q.poll();
				
				String BMIStr = q.poll();
				if(BMIStr.equals(""))
					BMI = 0;
				else
					BMI = Double.parseDouble(BMIStr);
				
				gender = q.poll();
				
				String MRNStr = q.poll();
				if(MRNStr.equals(""))
					MRN = 0;
				else
					MRN = Integer.parseInt(MRNStr);

				aRecord = new AnesthesiaRecord(DOB, DOS, weight, height,
						surgeon, BMI, gender, MRN);
				System.out.println(aRecord);
				aList.add(aRecord);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean equals(AnesthesiaRecord aR, BillingRecord bR) {

		boolean MRN, DOS, surgeon, gender;
		String DOS1, DOS2;
		SimpleDateFormat dateFormatter;

		if (aR.getMRN() == bR.getMRN())
			MRN = true;
		else
			MRN = false;

		dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

		DOS1 = dateFormatter.format(aR.getDOS().getTime());
		DOS2 = dateFormatter.format(bR.getDOS().getTime());

		DOS = DOS1.equals(DOS2);

		surgeon = aR.getSurgeon().equals(bR.getSurgeon());

		gender = aR.getGender().equals(bR.getGender());

		return (MRN && DOS && surgeon && gender);
	}

	public static LinkedList<MedicalRecord> merge(
			LinkedList<AnesthesiaRecord> aList,
			ConcurrentHashMap<String, BillingRecord> billingTable) {

		LinkedList<MedicalRecord> mList = new LinkedList<MedicalRecord>();
		AnesthesiaRecord aR = null;
		boolean theSame = false;
		//MedicalRecord mR = null;
		String key;
		BillingRecord bR = null;

		while (!aList.isEmpty()) {
			aR = aList.remove();
			key = aR.createKey();
			bR = billingTable.get(key);
			// Error-checking
			if (bR != null) {
				theSame = equals(aR, bR);
				if (theSame)
					mList.add(new MedicalRecord(aR, bR));
			}
		}

		return mList;
	}
	
	public static void main(String[] args) throws IOException {
		// Initialize table from CSV
		ConcurrentHashMap<String, BillingRecord> billingTable = new ConcurrentHashMap<String, BillingRecord>();
		importToConcurrentHashMap("bRecords.csv", billingTable);

		LinkedList<AnesthesiaRecord> anesthesiaList = new LinkedList<AnesthesiaRecord>();
		importToLinkedList("aRecords.csv", anesthesiaList);

		LinkedList<AnesthesiaRecord> tempList = new LinkedList<AnesthesiaRecord>(
				anesthesiaList);

		System.out.println();
		
		//Debug
		//Integer wtf = null;

		/*// Test that the table has the values
		System.out.println("BillingTable Contents:");
		Enumeration<Integer> keys = billingTable.keys();
		while (keys.hasMoreElements()) {
			wtf = keys.nextElement();
			System.out.println("Key: " + wtf.toString());
			System.out.println(billingTable.get(wtf));
		}
		// Test that the list has the values
		System.out.println("AnesthesiaList Contents:");
		while (!anesthesiaList.isEmpty()) {
			System.out.println(anesthesiaList.remove());
		}*/
		
		System.out.println(billingTable.get(new Integer(2043733)));

		LinkedList<MedicalRecord> mList = merge(tempList, billingTable);
		
		CSVWriter writer = new CSVWriter(new FileWriter("/Users/Shahein/Desktop/merged.csv"));
	     // feed in your array (or convert your data to an array)
		String[] entries = null;
	    while (!mList.isEmpty()) {
	    	entries = mList.remove().toString().split("~");
	    	writer.writeNext(entries);
	    }
		writer.close();
		
		// Test that the list has the values
		System.out.println("MRList Contents:");
		while (!mList.isEmpty()) {
			System.out.println(mList.remove());
		}
		
		/*
		// Test that equals works correctly
		int matchCount = 0;
		AnesthesiaRecord tempR = null;
		boolean theSame = false;
		MedicalRecord[] mR = new MedicalRecord[3];
		Integer MRN;

		while (!tempList.isEmpty()) {
			tempR = tempList.remove();
			MRN = new Integer(tempR.getMRN());
			// Error-checking
			if (billingTable.get(MRN) != null) {
				theSame = equals(tempR, billingTable.get(MRN));
				if (theSame)
					mR[matchCount++] = new MedicalRecord(tempR,
							billingTable.get(MRN));
				// Debug
				System.out.println("Are they the same? " + theSame);
				System.out.println(matchCount + " records are the same");
			}
		}

		System.out.println(mR[0]);
		System.out.println(mR[1]);
		System.out.println(mR[2]);
*/

		/*
		 * while (!tempList.isEmpty()) { tempR = tempList.remove();
		 * 
		 * if(billingTable.containsKey(Integer.valueOf(tempR.getMRN()))) {
		 * theSame = equals(tempR,
		 * billingTable.get(Integer.valueOf(tempR.getMRN()))); }
		 * 
		 * if(theSame) { matchCount++; mR[matchCount - 1] = new
		 * MedicalRecord(tempR,
		 * billingTable.get(Integer.valueOf(tempR.getMRN()))); } }
		 * 
		 * System.out.println("Matches?"); System.out.println(mR[0]);
		 * System.out.println(mR[1]); System.out.println(mR[2]);
		 */
	}
}
