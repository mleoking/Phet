package au.com.bytecode.opencsv;

/**
 Copyright 2005 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

public class OpencsvTest extends TestCase {

	private File tempFile = null;
	private CSVWriter writer = null;
	private CSVReader reader = null;

	protected void setUp() throws Exception {
		super.setUp();

		try {
			tempFile = File.createTempFile("csvWriterTest", ".csv");
			tempFile.deleteOnExit();
			// System.out.println("filename: "+tempFile.getAbsolutePath());
		} catch (IOException e) {
			fail();
		}
	}

	/**
	 * Test the full cycle of write-read
	 * 
	 */
	public void testWriteRead() throws IOException {
		final String[][] data = new String[][]{{"hello, a test", "one nested \" test"}, {"\"\"", "test", null, "8"}};

		writer = new CSVWriter(new FileWriter(tempFile));
		for (int i = 0; i < data.length; i++) {
			writer.writeNext(data[i]);
		}
		writer.close();

		reader = new CSVReader(new FileReader(tempFile));

		String[] line;
		for (int row = 0; (line = reader.readNext()) != null; row++) {
			assertTrue(line.length == data[row].length);

			for (int col = 0; col < line.length; col++) {
				if (data[row][col] == null) {
					assertTrue(line[col].equals(""));
				} else {
					assertTrue(line[col].equals(data[row][col]));
				}
			}
		}

		reader.close();
	}
}
