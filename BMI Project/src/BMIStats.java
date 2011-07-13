// 2011-07-08

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class BMIStats {
	/*
	 * Takes a CSV from Billing Database and imports to ConcurrentHashMap --Uses
	 * CSVReader class to slurp up each line in the CSV file, putting each line
	 * into a queue (LinkedList), then popping off each element as needed. The
	 * old array implementation of this was terrible.
	 */
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

				// System.out.println("Immediately from table");
				// System.out.println(table.get(new Integer(MRN)));

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
				if (tempDate.length == 3)
					DOS.set(Integer.parseInt(tempDate[2]) + 2000,
							Integer.parseInt(tempDate[0]) - 1,
							Integer.parseInt(tempDate[1]));
				else
					DOS.set(0, 0, 0);

				String weightStr = q.poll();
				if (weightStr.equals(""))
					weight = 0;
				else
					weight = Double.parseDouble(weightStr);

				String heightStr = q.poll();
				if (heightStr.equals(""))
					height = 0;
				else
					height = Double.parseDouble(heightStr);

				surgeon = q.poll();

				String BMIStr = q.poll();
				if (BMIStr.equals(""))
					BMI = 0;
				else
					BMI = Double.parseDouble(BMIStr);

				gender = q.poll();

				String MRNStr = q.poll();
				if (MRNStr.equals(""))
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
		// MedicalRecord mR = null;
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
	
	public static void printMenu(String input) {
		if(input.equals("")) {
			System.out.println("Menu:");
			System.out.println("1) Load Files");
			System.out.println("2) Analysis Settings");
			System.out.println("3) Write to File");
			System.out.println("Q) Quit");
		}
		else if (input.equals("1"))
		{
			System.out.println("1) Load Anesthesia and Billing databases separately");
			System.out.println("2) Load a pre-merged database");
		}
		else if(input.equals("2"))
		{
			System.out.println("1) Merge Only");
			System.out.println("2) CPT/BMI Analysis");
			System.out.println("3) CPT/BMI by Surgeon");
			System.out.println("4) CPT/BMI by Service");
		}
		else if(input.equals("3"))
		{
			System.out.println("1) Merge Only");
			System.out.println("2) CPT/BMI Analysis");
			System.out.println("3) CPT/BMI by Surgeon");
			System.out.println("4) CPT/BMI by Service");
		}
	}

	public static void writeToCSV(LinkedList mR, String filename)
			throws IOException {
		Iterator iter = mR.iterator();
		// Initialize CSVWriter
		CSVWriter writer = new CSVWriter(new FileWriter("/Users/Shahein/Desktop/" + filename));
		// feed in your array (or convert your data to an array)
		String[] entries = null;
		while (iter.hasNext()) {
			entries = iter.next().toString().split("~");
			writer.writeNext(entries);
		}
		writer.close();
	}
	
	public static LinkedList<CPTCode> analyzeCPT(LinkedList<MedicalRecord> mList) {
		Iterator<MedicalRecord> iter = mList.iterator();
		MedicalRecord tempMR;
		String[][] tempProcedures;
		CPTCode tempCode;
		ConcurrentHashMap<String, CPTCode> CPTMap = new ConcurrentHashMap<String, CPTCode>();
		
		while (iter.hasNext()) {
			tempMR = (MedicalRecord) iter.next();
			tempProcedures = tempMR.getProcedure();

			for (int i = 0; i < 22; i++) {
				tempCode = new CPTCode(tempProcedures[i][0],
						tempProcedures[i][1]);
				// If code not in database, put code in and associate this
				// patient's bmi with it
				// Else, retrieve CPTCode and associate this patient's BMI
				if (!CPTMap.containsKey(tempCode.getCPTCode())) {
					tempCode.addBMI(tempMR.getBMI());
					CPTMap.put(tempCode.getCPTCode(), tempCode);
				} else {
					tempCode = CPTMap.get(tempCode.getCPTCode());
					tempCode.addBMI(tempMR.getBMI());
				}
			}
		}
		
		LinkedList<CPTCode> CPTCodes = new LinkedList<CPTCode>(CPTMap.values());
		return CPTCodes;
	}
	
	public static LinkedList<CPTCode> analyzeCPTbySurgeon(LinkedList<MedicalRecord> mList) {
		Iterator<MedicalRecord> iter = mList.iterator();
		MedicalRecord tempMR;
		String[][] tempProcedures;
		String tempSurgeon;
		CPTCode tempCode;
		ConcurrentHashMap<String, CPTCode> CPTMap = new ConcurrentHashMap<String, CPTCode>();

		while (iter.hasNext()) {
			tempMR = (MedicalRecord) iter.next();
			tempProcedures = tempMR.getProcedure();
			tempSurgeon = tempMR.getSurgeon();	

			for (int i = 0; i < 22; i++) {
				tempCode = new CPTCode(tempProcedures[i][0],
						tempProcedures[i][1], tempSurgeon);
				// A note on keys in the hashmap:  
				// Keys are strings composed of <Surgeon>~CPTXX
				// *******************************************************
				// If code not in database, put code in and associate this
				// patient's bmi with it
				// Else, retrieve CPTCode and associate this patient's BMI
				if (!CPTMap.containsKey(tempSurgeon + "~" + tempCode.getCPTCode())) {
					tempCode.addBMI(tempMR.getBMI());
					CPTMap.put(tempSurgeon + "~" + tempCode.getCPTCode(), tempCode);
				} else {
					tempCode = CPTMap.get(tempSurgeon + "~" + tempCode.getCPTCode());
					tempCode.addBMI(tempMR.getBMI());
				}
			}
		}
		
		LinkedList<CPTCode> CPTCodes = new LinkedList<CPTCode>(CPTMap.values());
		return CPTCodes;
	}
	
	public static LinkedList<CPTCode> analyzeCPTbyService(LinkedList<MedicalRecord> mList) {
		Iterator<MedicalRecord> iter = mList.iterator();
		MedicalRecord tempMR;
		String[][] tempProcedures;
		String tempSurgeon;
		CPTCode tempCode;
		String service = "";
		ConcurrentHashMap<String, CPTCode> CPTMap = new ConcurrentHashMap<String, CPTCode>();

		while (iter.hasNext()) {
			tempMR = (MedicalRecord) iter.next();
			tempProcedures = tempMR.getProcedure();
			tempSurgeon = tempMR.getSurgeon();
			
			// Surgeon->Service Assignments
			// Kind of an ugly, ugly hack, because dealing with the surgeons that fit in multiple services cannot be dealt with elegantly.
			// As it stands, parse out the surgeons that are on one service.
			// If it happens to be one of the three that are weird, deal with them as special cases when creating CPT codes. 
			// They have the same thing happen, just twice, because the keys for the hashmap are Service~CPTXX, instead of Surgeon-CPTXX
			if(tempSurgeon.equals("Abdeen, Ayesha MD") || tempSurgeon.equals("Ayres, Douglas MD"))
				service = "Arthroplasty";
			else if (tempSurgeon.equals("Ramappa, Arun MD"))
				service = "Sports & Shoulder";
			else if (tempSurgeon.equals("Anderson, Megan MD") || tempSurgeon.equals("Gebhardt, Mark MD"))
				service = "Tumor";
			else if (tempSurgeon.equals("Duggal, Naven MD"))
				service = "Foot & Ankle";
			else if (tempSurgeon.equals("Day, Charles MD") || tempSurgeon.equals("Rozenthal, Tamara MD"))
				service = "Hand";
			else if (tempSurgeon.equals("McGuire, Kevin J. MD") || tempSurgeon.equals("White, Andrew MD"))
				service = "Spine";
			else if (tempSurgeon.equals("Rodriguez, Edward MD") || tempSurgeon.equals("Appleton, Paul T. MD"))
				service = "Trauma";

			for (int i = 0; i < 22; i++) {
				tempCode = new CPTCode(tempProcedures[i][0],
						tempProcedures[i][1], service);
				// If code not in database, put code in and associate this
				// patient's bmi with it
				// Else, retrieve CPTCode and associate this patient's BMI
				if (tempSurgeon.equals("Davis, Robert MD"))
				{
					if (!CPTMap.containsKey("Arthroplasty" + "~" + tempCode.getCPTCode())) {
						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Arthroplasty");
						CPTMap.put("Arthroplasty" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Arthroplasty" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}
					
					if (!CPTMap.containsKey("Sports & Shoulder" + "~" + tempCode.getCPTCode())) {
						tempCode = new CPTCode(tempProcedures[i][0],
								tempProcedures[i][1], service);
						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Sports & Shoulder");
						CPTMap.put("Sports & Shoulder" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Sports & Shoulder" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}

				}
				else if (tempSurgeon.equals("DeAngelis, Joseph P. MD"))
				{
					if (!CPTMap.containsKey("Sports & Shoulder" + "~" + tempCode.getCPTCode())) {
						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Sports & Shoulder");
						CPTMap.put("Sports & Shoulder" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Sports & Shoulder" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}
					
					if (!CPTMap.containsKey("Trauma" + "~" + tempCode.getCPTCode())) {
						tempCode = new CPTCode(tempProcedures[i][0],
								tempProcedures[i][1], service);
						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Trauma");
						CPTMap.put("Trauma" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Trauma" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}
				}
				else if (tempSurgeon.equals("Duggal, Naven MD"))
				{
					if (!CPTMap.containsKey("Foot & Ankle" + "~" + tempCode.getCPTCode())) {
						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Foot & Ankle");
						CPTMap.put("Foot & Ankle" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Foot & Ankle" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}
					
					if (!CPTMap.containsKey("Trauma" + "~" + tempCode.getCPTCode())) {
						tempCode = new CPTCode(tempProcedures[i][0],
								tempProcedures[i][1], service);
						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Trauma");
						CPTMap.put("Trauma" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Trauma" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}
				}
				else { 
					if (!CPTMap.containsKey(service + "~" + tempCode.getCPTCode())) {
					tempCode.addBMI(tempMR.getBMI());
					CPTMap.put(service + "~" + tempCode.getCPTCode(), tempCode);
					} else {
					tempCode = CPTMap.get(service + "~" + tempCode.getCPTCode());
					tempCode.addBMI(tempMR.getBMI());
					}
				}
			}
			service = "";
		}
		
		LinkedList<CPTCode> CPTCodes = new LinkedList<CPTCode>(CPTMap.values());
		return CPTCodes;
	}

	public static void main(String[] args) throws IOException {
		String input = "";
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		ConcurrentHashMap<String, BillingRecord> billingTable = null;
		LinkedList<AnesthesiaRecord> anesthesiaList = null;
		LinkedList<MedicalRecord> mList = null;
		LinkedList<CPTCode> CPTCodes = null;
		
		while(!input.equals("q") || !input.equals("Q")) {
			input = "";
			printMenu(input);
			input = br.readLine();
			
			// Load Files
			if(input.equals("1")) {
				printMenu(input);
				input = br.readLine();
				
				if(input.equals("1")) {
					// Load Separately
					
					// Initialize table from billingCSV
					billingTable = new ConcurrentHashMap<String, BillingRecord>();
					importToConcurrentHashMap("bRecords.csv", billingTable);

					// Initialize list from anesthesiaCSV
					anesthesiaList = new LinkedList<AnesthesiaRecord>();
					importToLinkedList("aRecords.csv", anesthesiaList);
					
					System.out.println("Load from aRecords.csv and bRecords.csv successful.");
				}
				else if(input.equals("2")) {
					// Load pre-merged
				}
				else
					System.out.println("Invalid input. Please try again");
			}
			// Analysis Settings
			else if(input.equals("2")) {
				printMenu(input);
				input = br.readLine();
				
				if(input.equals("1")) {
					// Merge Only
					// Merge and create mList
					mList = merge(anesthesiaList, billingTable);
				}
				else if(input.equals("2")) {
					// CPT/BMI Analysis
					// Merge and create mList
					mList = merge(anesthesiaList, billingTable);
					CPTCodes = analyzeCPT(mList);
				}
				else if(input.equals("3")) {
					// CPT/BMI by Surgeon
					// Merge and create mList
					mList = merge(anesthesiaList, billingTable);
					CPTCodes = analyzeCPTbySurgeon(mList);
				}
				else if(input.equals("4")) {
					// CPT/BMI by Service
					// Merge and create mList
					mList = merge(anesthesiaList, billingTable);
					CPTCodes = analyzeCPTbyService(mList);
				}
				else
					System.out.println("Invalid input. Please try again");
			}
			// Write to File
			else if(input.equals("3")) {
				System.out.println("Please type the filename you would like to use");
				// Filename input
				input = br.readLine();
				if(CPTCodes != null)
					writeToCSV(CPTCodes, input);
				else
					writeToCSV(mList, input);
			}
			else
				System.out.println("Invalid input. Please try again");
			
		// Initialize table from billingCSV
		//ConcurrentHashMap<String, BillingRecord> billingTable = new ConcurrentHashMap<String, BillingRecord>();
		//importToConcurrentHashMap("bRecords.csv", billingTable);

		// Initialize list from anesthesiaCSV
		//LinkedList<AnesthesiaRecord> anesthesiaList = new LinkedList<AnesthesiaRecord>();
		//importToLinkedList("aRecords.csv", anesthesiaList);

		// Extra anesthesia list for debug
		//LinkedList<AnesthesiaRecord> tempList = new LinkedList<AnesthesiaRecord>(
		//		anesthesiaList);

		//System.out.println();

		// Merge and create mList
		//LinkedList<MedicalRecord> mList = merge(tempList, billingTable);

		// Let's get cracking on calculating that average BMI
		// First need to initialize an ArrayList of CPTCode, taken from mList
		// Then iterate across that ArrayList, calculating the average BMI
		// Then write that all out to file

		// LinkedList<MedicalRecord> tempMList = new LinkedList<MedicalRecord>(
		// mList);

		/*Iterator<MedicalRecord> iter = mList.iterator();
		MedicalRecord tempMR;
		ConcurrentHashMap<String, CPTCode> CPTMap = new ConcurrentHashMap<String, CPTCode>();
		String[][] tempProcedures;
		CPTCode tempCode;
		String tempSurgeon;
		String service = "";

		while (iter.hasNext()) {
			tempMR = (MedicalRecord) iter.next();
			tempProcedures = tempMR.getProcedure();
			tempSurgeon = tempMR.getSurgeon();
			
			// Surgeon->Service Assignments
			if(tempSurgeon.equals("Abdeen, Ayesha MD") || tempSurgeon.equals("Ayres, Douglas MD"))
				service = "Arthroplasty";
			else if (tempSurgeon.equals("Ramappa, Arun MD"))
				service = "Sports & Shoulder";
			else if (tempSurgeon.equals("Anderson, Megan MD") || tempSurgeon.equals("Gebhardt, Mark MD"))
				service = "Tumor";
			else if (tempSurgeon.equals("Duggal, Naven MD"))
				service = "Foot & Ankle";
			else if (tempSurgeon.equals("Day, Charles MD") || tempSurgeon.equals("Rozenthal, Tamara MD"))
				service = "Hand";
			else if (tempSurgeon.equals("McGuire, Kevin J. MD") || tempSurgeon.equals("White, Andrew MD"))
				service = "Spine";
			else if (tempSurgeon.equals("Rodriguez, Eduard MD") || tempSurgeon.equals("Appleton, Paul T. MD"))
				service = "Trauma";

			for (int i = 0; i < 22; i++) {
				tempCode = new CPTCode(tempProcedures[i][0],
						tempProcedures[i][1], service);
				// If code not in database, put code in and associate this
				// patient's bmi with it
				// Else, retrieve CPTCode and associate this patient's BMI
				if (tempSurgeon.equals("Davis, Robert MD"))
				{
					if (!CPTMap.containsKey("Arthroplasty" + "~" + tempCode.getCPTCode())) {
						tempCode.addBMI(tempMR.getBMI());
						CPTMap.put("Arthroplasty" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Arthroplasty" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}
					
					if (!CPTMap.containsKey("Sports & Shoulder" + "~" + tempCode.getCPTCode())) {
						tempCode.addBMI(tempMR.getBMI());
						CPTMap.put("Sports & Shoulder" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Sports & Shoulder" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}

				}
				else if (tempSurgeon.equals("DeAngelis, Joseph P. MD"))
				{
					if (!CPTMap.containsKey("Sports & Shoulder" + "~" + tempCode.getCPTCode())) {
						tempCode.addBMI(tempMR.getBMI());
						CPTMap.put("Sports & Shoulder" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Sports & Shoulder" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}
					
					if (!CPTMap.containsKey("Trauma" + "~" + tempCode.getCPTCode())) {
						tempCode.addBMI(tempMR.getBMI());
						CPTMap.put("Trauma" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Trauma" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}
				}
				else if (tempSurgeon.equals("Duggal, Naven MD"))
				{
					if (!CPTMap.containsKey("Foot & Ankle" + "~" + tempCode.getCPTCode())) {
						tempCode.addBMI(tempMR.getBMI());
						CPTMap.put("Foot & Ankle" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Foot & Ankle" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}
					
					if (!CPTMap.containsKey("Trauma" + "~" + tempCode.getCPTCode())) {
						tempCode.addBMI(tempMR.getBMI());
						CPTMap.put("Trauma" + "~" + tempCode.getCPTCode(), tempCode);
					} else {
						tempCode = CPTMap.get("Trauma" + "~" + tempCode.getCPTCode());
						tempCode.addBMI(tempMR.getBMI());
					}
				}
				else if (!CPTMap.containsKey(service + "~" + tempCode.getCPTCode())) {
					tempCode.addBMI(tempMR.getBMI());
					CPTMap.put(service + "~" + tempCode.getCPTCode(), tempCode);
				} else {
					tempCode = CPTMap.get(service + "~" + tempCode.getCPTCode());
					tempCode.addBMI(tempMR.getBMI());
				}
			}
		}
		
		LinkedList<CPTCode> CPTCodes = new LinkedList<CPTCode>(CPTMap.values());
		Iterator<CPTCode> iter2;
		iter2 = CPTCodes.iterator();*/
	
		// Initialize CSVWriter
		//CSVWriter writer = new CSVWriter(new FileWriter("/Users/Shahein/Desktop/CPTbyService.csv"));
		// feed in your array (or convert your data to an array)
		//String[] entries = null;
		//while (iter2.hasNext()) {
		//	entries = iter2.next().toString().split("~");
		//	writer.writeNext(entries);
		//}
		//writer.close();
		
		// Write info out to file.
		//writeToCSV(mList, "/Users/Shahein/Desktop/merged.csv");

		/*
		 * CSVWriter writer = new CSVWriter(new FileWriter(
		 * "/Users/Shahein/Desktop/merged.csv")); // feed in your array (or
		 * convert your data to an array) String[] entries = null; while
		 * (!mList.isEmpty()) { entries = mList.remove().toString().split("~");
		 * writer.writeNext(entries); } writer.close();
		 */

		// Test that the list has the values
		//System.out.println("MRList Contents:");
		//while (!mList.isEmpty()) {
		//	System.out.println(mList.remove());
		//}
		}
	}
}
