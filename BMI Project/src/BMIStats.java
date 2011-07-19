// 2011-07-18

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class BMIStats {
	public static LinkedList<MedicalRecord> addModifiers(
			LinkedList<MedicalRecord> medList,
			LinkedList<ModifierRecord> modList) {

		// ConcurrentHashMap<String, MedicalRecord> medTable = new
		// ConcurrentHashMap<String, MedicalRecord>();
		ConcurrentHashMap<String, ModifierRecord> modTable = new ConcurrentHashMap<String, ModifierRecord>();
		ModifierRecord tempModR = null;
		MedicalRecord tempMedR = null;
		ArrayList<CPTCode> tempCodes = null;

		// Put modifier list into a hashmap
		Iterator<ModifierRecord> iter = modList.iterator();
		while (iter.hasNext()) {
			tempModR = iter.next();
			// Places each ModifierRecord into the hashmap, using the createkey
			// string plus the CPT code the ModifierRecord holds
			modTable.put(tempModR.createKey() + "~"
					+ tempModR.getProcedure().get(0).getCode(), tempModR);
		}

		// Empty this out to prevent any weird pointers hanging around
		tempModR = null;

		// Iterate through the LinkedList of MedicalRecords, getting the CPT
		// Arrays of each record. Query each CPT code from the ModifierRecord
		// hashmap.
		ArrayList<CPTCode> CPTCodes = null;
		Iterator<MedicalRecord> iter2 = medList.iterator();
		while (iter2.hasNext()) {
			tempMedR = iter2.next();
			// Get Procedure ArrayList from the Medical Record
			CPTCodes = tempMedR.getProcedure();
			
			// Iterate through Patient's CPTCode Arraylist
			CPTCode tempPatientCode = null;
			ModifierRecord tempModRecord = null;
			Iterator<CPTCode> iter3 = CPTCodes.iterator();
			while (iter3.hasNext()) {
				tempPatientCode = iter3.next();
				// Query Hashmap (key = MedR-key + ~CPTXX)
				tempModRecord = modTable.get(tempMedR.createKey() + "~" + tempPatientCode.getCode());
				// If they match (protecting against calling null)
				if (tempModRecord != null && (tempModRecord.createKey() + "~" + tempModRecord.getProcedure().get(0).getCode()).equals(tempMedR.createKey() + "~" + tempPatientCode.getCode())) {
					// Add in the modifiers
					tempPatientCode.setMod1(tempModRecord.getMod1());
					tempPatientCode.setMod2(tempModRecord.getMod2());
					tempPatientCode.setCharge(tempModRecord.getCharge());
					
					// Set the Modifier-22 Flag in the Medical record
					if(tempModRecord.getMod1().equals("22") || tempModRecord.getMod2().equals("22"))
						tempMedR.setMod22(true);
				}
				// If they don't match, let's initialize just in case and
				// prevent from column misalignment
				else {
					tempPatientCode.setMod1("");
					tempPatientCode.setMod2("");
					tempPatientCode.setCharge(0);
				}
			}
			CPTCodes = null;
		}

		// Convert the MedTable back to a LinkedList
		// May not have to do that after all, due to the quasi pointers we have
		// going on...ughh...
		// Debug
		// System.out.println(medTable.get("01/01/2010~Abdeen, Ayesha MD~2252977~M"));
		// Iterator x = medList.iterator();
		// while(x.hasNext()) {
		// System.out.println(x.next());
		// }
		return medList;
	}

	public static LinkedList<CPTCode> analyzeCPT(LinkedList<MedicalRecord> mList) {
		MedicalRecord tempMR;
		ArrayList<CPTCode> procedure;
		CPTCode tempCode;
		ConcurrentHashMap<String, CPTCode> CPTMap = new ConcurrentHashMap<String, CPTCode>();

		Iterator<MedicalRecord> iter = mList.iterator();
		while (iter.hasNext()) {
			tempMR = (MedicalRecord) iter.next();
			procedure = tempMR.getProcedure();

			for (int i = 0; i < procedure.size(); i++) {
				tempCode = procedure.get(i);
				// If code not in database, put code in and associate this
				// patient's bmi with it
				// Else, retrieve CPTCode and associate this patient's BMI
				if (!CPTMap.containsKey(tempCode.getCode())) {
					tempCode.addBMI(tempMR.getBMI());
					CPTMap.put(tempCode.getCode(), tempCode);
				} else {
					tempCode = CPTMap.get(tempCode.getCode());
					tempCode.addBMI(tempMR.getBMI());
				}
			}
		}

		LinkedList<CPTCode> CPTCodes = new LinkedList<CPTCode>(CPTMap.values());
		return CPTCodes;
	}

	public static LinkedList<CPTCode> analyzeCPTbySurgeon(
			LinkedList<MedicalRecord> mList) {
		MedicalRecord tempMR;
		ArrayList<CPTCode> procedure;
		String tempSurgeon;
		CPTCode tempCode;
		ConcurrentHashMap<String, CPTCode> CPTMap = new ConcurrentHashMap<String, CPTCode>();

		Iterator<MedicalRecord> iter = mList.iterator();
		while (iter.hasNext()) {
			tempMR = (MedicalRecord) iter.next();
			procedure = tempMR.getProcedure();
			tempSurgeon = tempMR.getSurgeon();

			for (int i = 0; i < procedure.size(); i++) {
				tempCode = procedure.get(i);
				tempCode.setSurgeon(tempSurgeon);

				// A note on keys in the hashmap:
				// Keys are strings composed of <Surgeon>~CPTXX
				// *******************************************************
				// If code not in database, put code in and associate this
				// patient's BMI with it
				// Else, retrieve CPTCode and associate this patient's BMI
				if (!CPTMap.containsKey(tempSurgeon + "~" + tempCode.getCode())) {
					tempCode.addBMI(tempMR.getBMI());
					CPTMap.put(tempSurgeon + "~" + tempCode.getCode(), tempCode);
				} else {
					tempCode = CPTMap.get(tempSurgeon + "~"
							+ tempCode.getCode());
					tempCode.addBMI(tempMR.getBMI());
				}
			}
		}
		LinkedList<CPTCode> CPTCodes = new LinkedList<CPTCode>(CPTMap.values());
		return CPTCodes;
	}

	public static LinkedList<CPTCode> analyzeCPTbyService(
			LinkedList<MedicalRecord> mList) {
		MedicalRecord tempMR;
		ArrayList<CPTCode> procedure;
		String tempSurgeon;
		CPTCode tempCode;
		String service = "";
		ConcurrentHashMap<String, CPTCode> CPTMap = new ConcurrentHashMap<String, CPTCode>();

		Iterator<MedicalRecord> iter = mList.iterator();
		while (iter.hasNext()) {
			tempMR = (MedicalRecord) iter.next();
			procedure = tempMR.getProcedure();
			tempSurgeon = tempMR.getSurgeon();

			// Surgeon->Service Assignments
			// Kind of an ugly, ugly hack, because dealing with the surgeons
			// that fit in multiple services cannot be dealt with elegantly.
			// As it stands, parse out the surgeons that are on one service.
			// If it happens to be one of the three that are weird, deal with
			// them as special cases when creating CPT codes.
			// They have the same thing happen, just twice, because the keys for
			// the hashmap are Service~CPTXX, instead of Surgeon-CPTXX
			if (tempSurgeon.equals("Abdeen, Ayesha MD")
					|| tempSurgeon.equals("Ayres, Douglas MD"))
				service = "Arthroplasty";
			else if (tempSurgeon.equals("Ramappa, Arun MD"))
				service = "Sports & Shoulder";
			else if (tempSurgeon.equals("Anderson, Megan MD")
					|| tempSurgeon.equals("Gebhardt, Mark MD"))
				service = "Tumor";
			else if (tempSurgeon.equals("Duggal, Naven MD"))
				service = "Foot & Ankle";
			else if (tempSurgeon.equals("Day, Charles MD")
					|| tempSurgeon.equals("Rozenthal, Tamara MD"))
				service = "Hand";
			else if (tempSurgeon.equals("McGuire, Kevin J. MD")
					|| tempSurgeon.equals("White, Andrew MD"))
				service = "Spine";
			else if (tempSurgeon.equals("Rodriguez, Edward MD")
					|| tempSurgeon.equals("Appleton, Paul T. MD"))
				service = "Trauma";

			for (int i = 0; i < procedure.size(); i++) {
				tempCode = procedure.get(i);
				tempCode.setSurgeon(service);
				// If code not in database, put code in and associate this
				// patient's bmi with it
				// Else, retrieve CPTCode and associate this patient's BMI
				if (tempSurgeon.equals("Davis, Robert MD")) {
					if (!CPTMap.containsKey("Arthroplasty" + "~"
							+ tempCode.getCode())) {
						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Arthroplasty");
						CPTMap.put("Arthroplasty" + "~" + tempCode.getCode(),
								tempCode);
					} else {
						tempCode = CPTMap.get("Arthroplasty" + "~"
								+ tempCode.getCode());
						tempCode.addBMI(tempMR.getBMI());
					}

					if (!CPTMap.containsKey("Sports & Shoulder" + "~"
							+ tempCode.getCode())) {
						tempCode = procedure.get(i);
						tempCode.setSurgeon(service);

						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Sports & Shoulder");
						CPTMap.put(
								"Sports & Shoulder" + "~" + tempCode.getCode(),
								tempCode);
					} else {
						tempCode = CPTMap.get("Sports & Shoulder" + "~"
								+ tempCode.getCode());
						tempCode.addBMI(tempMR.getBMI());
					}

				} else if (tempSurgeon.equals("DeAngelis, Joseph P. MD")) {
					if (!CPTMap.containsKey("Sports & Shoulder" + "~"
							+ tempCode.getCode())) {
						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Sports & Shoulder");
						CPTMap.put(
								"Sports & Shoulder" + "~" + tempCode.getCode(),
								tempCode);
					} else {
						tempCode = CPTMap.get("Sports & Shoulder" + "~"
								+ tempCode.getCode());
						tempCode.addBMI(tempMR.getBMI());
					}

					if (!CPTMap
							.containsKey("Trauma" + "~" + tempCode.getCode())) {
						tempCode = procedure.get(i);
						tempCode.setSurgeon(service);

						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Trauma");
						CPTMap.put("Trauma" + "~" + tempCode.getCode(),
								tempCode);
					} else {
						tempCode = CPTMap.get("Trauma" + "~"
								+ tempCode.getCode());
						tempCode.addBMI(tempMR.getBMI());
					}
				} else if (tempSurgeon.equals("Duggal, Naven MD")) {
					if (!CPTMap.containsKey("Foot & Ankle" + "~"
							+ tempCode.getCode())) {
						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Foot & Ankle");
						CPTMap.put("Foot & Ankle" + "~" + tempCode.getCode(),
								tempCode);
					} else {
						tempCode = CPTMap.get("Foot & Ankle" + "~"
								+ tempCode.getCode());
						tempCode.addBMI(tempMR.getBMI());
					}

					if (!CPTMap
							.containsKey("Trauma" + "~" + tempCode.getCode())) {
						tempCode = procedure.get(i);
						tempCode.setSurgeon(service);

						tempCode.addBMI(tempMR.getBMI());
						tempCode.setSurgeon("Trauma");
						CPTMap.put("Trauma" + "~" + tempCode.getCode(),
								tempCode);
					} else {
						tempCode = CPTMap.get("Trauma" + "~"
								+ tempCode.getCode());
						tempCode.addBMI(tempMR.getBMI());
					}
				} else {
					if (!CPTMap.containsKey(service + "~" + tempCode.getCode())) {
						tempCode.addBMI(tempMR.getBMI());
						CPTMap.put(service + "~" + tempCode.getCode(), tempCode);
					} else {
						tempCode = CPTMap.get(service + "~"
								+ tempCode.getCode());
						tempCode.addBMI(tempMR.getBMI());
					}
				}
			}
			service = "";
		}

		LinkedList<CPTCode> CPTCodes = new LinkedList<CPTCode>(CPTMap.values());
		return CPTCodes;
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
				// Debug
				System.out.println(aRecord);
				aList.add(aRecord);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Takes a CSV from Billing Database and imports to ConcurrentHashMap --Uses
	 * CSVReader class to slurp up each line in the CSV file, putting each line
	 * into a queue (LinkedList), then popping off each element as needed. The
	 * old array implementation of this was terrible.
	 */
	public static void importToConcurrentHashMap(String filename,
			ConcurrentHashMap<String, BillingRecord> table) throws IOException {
		BillingRecord bRecord;
		// Debug
		int linesRead = 0;

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
			ArrayList<ICD9Code> diagnosis = new ArrayList<ICD9Code>();
			ArrayList<CPTCode> procedure = new ArrayList<CPTCode>();

			System.out.println("BillingTable Construction");

			// For each line, convert the csv into a BillingRecord object and
			// add it to the passed cHashMap.
			while ((line = reader.readNext()) != null) {
				// Debug
				linesRead++;
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
				for (int i = 0; i < BillingRecord.DIAGNOSIS_NUMBER; i++) {
					diagnosis.add(new ICD9Code(q.poll(), q.poll()));
				}

				// Procedure fields
				for (int i = 0; i < BillingRecord.PROCEDURE_NUMBER; i++) {
					procedure.add(new CPTCode(q.poll(), q.poll()));
				}

				bRecord = new BillingRecord(MRN, patientName, Age, gender,
						surgeon, DOS, diagnosis, procedure);

				// Debug
				System.out.println(bRecord);

				table.put(bRecord.createKey(), bRecord);

				bRecord = null;
				diagnosis = new ArrayList<ICD9Code>();
				procedure = new ArrayList<CPTCode>();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println(linesRead + " lines read from bRecords.csv");
	}

	/*
	 * Takes a CSV from Billing Database and imports to ConcurrentHashMap --Uses
	 * CSVReader class to slurp up each line in the CSV file, putting each line
	 * into a queue (LinkedList), then popping off each element as needed. The
	 * old array implementation of this was terrible.
	 */
	public static void importModifierCodes(String filename,
			LinkedList<ModifierRecord> modList) throws IOException {
		// Try reading in the CPT Modifier Code CSV File
		try {
			CSVReader reader = new CSVReader(new FileReader(
					"/Users/shahein/Desktop/" + filename));
			// String to hold each line of file
			String[] line;
			// Queue for each field on each line
			LinkedList<String> q = new LinkedList<String>();
			// Variables for each ModifierRecord
			String MRN;
			String patientName;
			String gender;
			String surgeon;
			Calendar DOS;
			Calendar DOB;
			ArrayList<ICD9Code> diagnosis = new ArrayList<ICD9Code>();
			ArrayList<CPTCode> procedure = new ArrayList<CPTCode>();
			String mod1;
			String mod2;
			int charge;
			ModifierRecord mRecord = null;
			String tempDate[];

			while ((line = reader.readNext()) != null) {
				// Put the array of strings into a queue
				for (int i = 0; i < line.length; i++) {
					q.add(line[i]);
				}
				// Poll through the queue and read out data
				surgeon = q.poll();
				tempDate = q.poll().split("/");
				DOS = Calendar.getInstance();
				if (tempDate.length == 3)
					DOS.set(Integer.parseInt(tempDate[2]) + 2000,
							Integer.parseInt(tempDate[0]) - 1,
							Integer.parseInt(tempDate[1]));
				else
					DOS.set(0, 0, 0);

				MRN = q.poll();
				patientName = q.poll() + "," + q.poll();
				gender = q.poll();

				tempDate = q.poll().split("/");
				DOB = Calendar.getInstance();
				DOB.set(Integer.parseInt(tempDate[2]) + 1900,
						Integer.parseInt(tempDate[0]) - 1,
						Integer.parseInt(tempDate[1]));

				// Procedure fields
				for (int i = 0; i < ModifierRecord.PROCEDURE_NUMBER; i++) {
					procedure.add(new CPTCode(q.poll(), q.poll()));
				}

				// Diagnosis fields
				for (int i = 0; i < ModifierRecord.DIAGNOSIS_NUMBER; i++) {
					diagnosis.add(new ICD9Code(q.poll(), q.poll()));
				}

				mod1 = q.poll();
				mod2 = q.poll();

				String tempCharge = q.poll();

				charge = Integer.parseInt(tempCharge.replaceAll(",", ""));

				mRecord = new ModifierRecord(surgeon, DOS, MRN, patientName,
						gender, DOB, procedure, diagnosis, mod1, mod2, charge);
				// Debug
				System.out.println(mRecord);
				modList.add(mRecord);

				// Gotta clear em out and avoid the pointers...
				diagnosis = new ArrayList<ICD9Code>();
				procedure = new ArrayList<CPTCode>();

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		// Set up to read input from keyboard
		String input = "";
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		ConcurrentHashMap<String, BillingRecord> billingTable = null;
		LinkedList<AnesthesiaRecord> anesthesiaList = null;
		LinkedList<MedicalRecord> medList = null;
		LinkedList<CPTCode> CPTCodes = null;
		LinkedList<ModifierRecord> modList = null;

		while (!(input.equals("q")) && !(input.equals("Q"))) {
			input = "";
			printMenu(input);
			input = br.readLine();

			// Load Files
			if (input.equals("1")) {
				printMenu(input);
				input = br.readLine();

				if (input.equals("1")) {
					// Load Anesthesia

					// Initialize list from anesthesiaCSV
					anesthesiaList = new LinkedList<AnesthesiaRecord>();
					importToLinkedList("aRecords.csv", anesthesiaList);
					System.out.println(anesthesiaList.size()
							+ " Anesthesia Records loaded successfully.");
				} else if (input.equals("2")) {
					// Load Billing

					// Initialize table from billingCSV
					billingTable = new ConcurrentHashMap<String, BillingRecord>();
					importToConcurrentHashMap("bRecords.csv", billingTable);
					System.out.println(billingTable.size()
							+ " Billing Records loaded successfully.");
				} else if (input.equals("3")) {
					// Load Modifier
					modList = new LinkedList<ModifierRecord>();
					importModifierCodes("cRecords.csv", modList);
					System.out.println(modList.size()
							+ " Modifier Records loaded successfully.");
				} else if (input.equals("0")) {
					// Load Modifier
					anesthesiaList = new LinkedList<AnesthesiaRecord>();
					importToLinkedList("aRecords.csv", anesthesiaList);
					System.out.println(anesthesiaList.size()
							+ " Anesthesia Records loaded successfully.");
					billingTable = new ConcurrentHashMap<String, BillingRecord>();
					importToConcurrentHashMap("bRecords.csv", billingTable);
					System.out.println(billingTable.size()
							+ " Billing Records loaded successfully.");
					modList = new LinkedList<ModifierRecord>();
					importModifierCodes("cRecords.csv", modList);
					System.out.println(modList.size()
							+ " Modifier Records loaded successfully.");
				} else
					System.out.println("Invalid input. Please try again");
			}
			// Analysis Settings
			else if (input.equals("2")) {
				printMenu(input);
				input = br.readLine();

				if (input.equals("1")) {
					// Merging gets tricky, because we now have to merge all
					// three databases into the megadatabase
					// Merge Only
					// Merge and create medList
					medList = merge(anesthesiaList, billingTable);
					// importModifierCodes("modifierbmi.csv", mList);
				} else if (input.equals("2")) {
					// CPT/BMI Analysis
					// Merge and create medList
					medList = merge(anesthesiaList, billingTable);
					CPTCodes = analyzeCPT(medList);
				} else if (input.equals("3")) {
					// CPT/BMI by Surgeon
					// Merge and create medList
					medList = merge(anesthesiaList, billingTable);
					CPTCodes = analyzeCPTbySurgeon(medList);
				} else if (input.equals("4")) {
					// CPT/BMI by Service
					// Merge and create medList
					medList = merge(anesthesiaList, billingTable);
					CPTCodes = analyzeCPTbyService(medList);
				} else if (input.equals("5")) {
					// Add Modifiers
					// Merge and create medList
					medList = merge(anesthesiaList, billingTable);
					medList = addModifiers(medList, modList);
				} else
					System.out.println("Invalid input. Please try again.");
			}
			// Write to File
			else if (input.equals("3")) {
				System.out
						.println("Please type the filename you would like to use");
				// Filename input
				input = br.readLine();
				if (CPTCodes != null)
					writeToCSV(CPTCodes, input);
				else
					writeToCSV(medList, input);
			} else if (input.equals("q") || input.equals("Q"))
				System.out.println("Goodbye");
			else
				System.out.println("Invalid input. Please try again");
		}
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
		if (input.equals("")) {
			System.out.println("Menu:");
			System.out.println("1) Load Files");
			System.out.println("2) Analysis Settings");
			System.out.println("3) Write to File");
			System.out.println("Q) Quit");
		} else if (input.equals("1")) {
			System.out.println("1) Load Anesthesia database");
			System.out.println("2) Load Billing database");
			System.out.println("3) Load Modifier database");
			System.out.println("4) Load a pre-merged database");
		} else if (input.equals("2")) {
			System.out.println("1) Merge Only");
			System.out.println("2) CPT/BMI Analysis");
			System.out.println("3) CPT/BMI by Surgeon");
			System.out.println("4) CPT/BMI by Service");
			System.out.println("5) Add Modifiers");
		} else if (input.equals("3")) {
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
		CSVWriter writer = new CSVWriter(new FileWriter(
				"/Users/Shahein/Desktop/" + filename));
		// feed in your array (or convert your data to an array)
		String[] entries = null;
		while (iter.hasNext()) {
			entries = iter.next().toString().split("~");
			writer.writeNext(entries);
		}
		writer.close();
	}
}
