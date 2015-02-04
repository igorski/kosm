package edu.toronto.psi.vincent.util;



import java.io.*;


/**

 * Class for reading binary data from files to multidimensional arrays

 * and writing binary data to files from multidimensional arrays.

 *

 * <pre>

 * Copyright (C) 2005  Vincent Cheung (vincent@psi.toronto.edu, http://www.psi.toronto.edu/~vincent/)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.</pre>

 * 

 * @author <a href="mailto:vincent@psi.toronto.edu">Vincent Cheung</a>

 * @version 1.3 11/23/05

 */

public class ArrayReaderWriter {



	/**

	 * Writes an array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(double[] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			// get the 64-bit long representation of the double

			value = Double.doubleToLongBits(array[i]);



			// write the 64-bit long to the stream

			for(int n = 0; n < 8; n++) {

				file.write((byte)(value & 0xff));



				value >>= 8;

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(double[][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				// get the 64-bit long representation of the double

				value = Double.doubleToLongBits(array[i][j]);



				// write the 64-bit long to the stream

				for(int n = 0; n < 8; n++) {

					file.write((byte)(value & 0xff));



					value >>= 8;

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(double[][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				for(int k = 0; k < array[0][0].length; k++) {

					// get the 64-bit long representation of the double

					value = Double.doubleToLongBits(array[i][j][k]);



					// write the 64-bit long to the stream

					for(int n = 0; n < 8; n++) {

						file.write((byte)(value & 0xff));



						value >>= 8;

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(double[][][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length, array[0][0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						// get the 64-bit long representation of the double

						value = Double.doubleToLongBits(array[t][i][j][k]);



						// write the 64-bit long to the stream

						for(int n = 0; n < 8; n++) {

							file.write((byte)(value & 0xff));



							value >>= 8;

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(double[][][][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length, array[0][0][0].length, array[0][0][0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						for(int m = 0; m < array[0][0][0][0].length; m++) {

							// get the 64-bit long representation of the double

							value = Double.doubleToLongBits(array[t][i][j][k][m]);



							// write the 64-bit long to the stream

							for(int n = 0; n < 8; n++) {

								file.write((byte)(value & 0xff));



								value >>= 8;

							} // end for

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Reads an array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static double[] read1DArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		double[] array = null;

		int[] size = new int[1];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new double[size[0]];



		for(int i = 0; i < array.length; i++) {

			value = 0;



			for(int n = 0; n < 8; n++)

				value |= (long)(file.read() & 0xff) << (8 * n);



			array[i] = Double.longBitsToDouble(value);



		} // end for



		file.close();



		return(array);

	} // end read1DArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static double[][] read2DArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		double[][] array = null;

		int[] size = new int[2];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new double[size[0]][size[1]];



		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				value = 0;



				for(int n = 0; n < 8; n++)

					value |= (long)(file.read() & 0xff) << (8 * n);



				array[i][j] = Double.longBitsToDouble(value);



			} // end for

		} // end for



		file.close();



		return(array);

	} // end read2DArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static double[][][] read3DArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		double[][][] array = null;

		int[] size = new int[3];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new double[size[0]][size[1]][size[2]];



		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				for(int k = 0; k < array[0][0].length; k++) {

					value = 0;



					for(int n = 0; n < 8; n++)

						value |= (long)(file.read() & 0xff) << (8 * n);



					array[i][j][k] = Double.longBitsToDouble(value);



				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read3DArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static double[][][][] read4DArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		double[][][][] array = null;

		int[] size = new int[4];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new double[size[0]][size[1]][size[2]][size[3]];



		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						value = 0;



						for(int n = 0; n < 8; n++)

							value |= (long)(file.read() & 0xff) << (8 * n);



						array[t][i][j][k] = Double.longBitsToDouble(value);

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read4DArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static double[][][][][] read5DArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		double[][][][][] array = null;

		int[] size = new int[5];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new double[size[0]][size[1]][size[2]][size[3]][size[4]];



		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						for(int m = 0; m < array[0][0][0][0].length; m++) {

							value = 0;



							for(int n = 0; n < 8; n++)

								value |= (long)(file.read() & 0xff) << (8 * n);



							array[t][i][j][k][m] = Double.longBitsToDouble(value);

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read5DArray method





	// -------------------------------------------------------------------------------------

	// float arrays

	// -------------------------------------------------------------------------------------



	/**

	 * Writes an array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(float[] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			// get the 64-bit long representation of the double

			value = Double.doubleToLongBits((float)array[i]);



			// write the 64-bit long to the stream

			for(int n = 0; n < 8; n++) {

				file.write((byte)(value & 0xff));



				value >>= 8;

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(float[][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				// get the 64-bit long representation of the double

				value = Double.doubleToLongBits((float)array[i][j]);



				// write the 64-bit long to the stream

				for(int n = 0; n < 8; n++) {

					file.write((byte)(value & 0xff));



					value >>= 8;

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(float[][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				for(int k = 0; k < array[0][0].length; k++) {

					// get the 64-bit long representation of the double

					value = Double.doubleToLongBits((float)array[i][j][k]);



					// write the 64-bit long to the stream

					for(int n = 0; n < 8; n++) {

						file.write((byte)(value & 0xff));



						value >>= 8;

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(float[][][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length, array[0][0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						// get the 64-bit long representation of the double

						value = Double.doubleToLongBits((float)array[t][i][j][k]);



						// write the 64-bit long to the stream

						for(int n = 0; n < 8; n++) {

							file.write((byte)(value & 0xff));



							value >>= 8;

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(float[][][][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length, array[0][0][0].length, array[0][0][0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						for(int m = 0; m < array[0][0][0][0].length; m++) {

							// get the 64-bit long representation of the double

							value = Double.doubleToLongBits((float)array[t][i][j][k][m]);



							// write the 64-bit long to the stream

							for(int n = 0; n < 8; n++) {

								file.write((byte)(value & 0xff));



								value >>= 8;

							} // end for

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Reads an array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static float[] read1DFloatArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		float[] array = null;

		int[] size = new int[1];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new float[size[0]];



		for(int i = 0; i < array.length; i++) {

			value = 0;



			for(int n = 0; n < 8; n++)

				value |= (long)(file.read() & 0xff) << (8 * n);



			array[i] = (float)Double.longBitsToDouble(value);



		} // end for



		file.close();



		return(array);

	} // end read1DFloatArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static float[][] read2DFloatArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		float[][] array = null;

		int[] size = new int[2];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new float[size[0]][size[1]];



		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				value = 0;



				for(int n = 0; n < 8; n++)

					value |= (long)(file.read() & 0xff) << (8 * n);



				array[i][j] = (float)Double.longBitsToDouble(value);



			} // end for

		} // end for



		file.close();



		return(array);

	} // end read2DFloatArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static float[][][] read3DFloatArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		float[][][] array = null;

		int[] size = new int[3];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new float[size[0]][size[1]][size[2]];



		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				for(int k = 0; k < array[0][0].length; k++) {

					value = 0;



					for(int n = 0; n < 8; n++)

						value |= (long)(file.read() & 0xff) << (8 * n);



					array[i][j][k] = (float)Double.longBitsToDouble(value);



				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read3DFloatArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static float[][][][] read4DFloatArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		float[][][][] array = null;

		int[] size = new int[4];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new float[size[0]][size[1]][size[2]][size[3]];



		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						value = 0;



						for(int n = 0; n < 8; n++)

							value |= (long)(file.read() & 0xff) << (8 * n);



						array[t][i][j][k] = (float)Double.longBitsToDouble(value);

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read4DFloatArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static float[][][][][] read5DFloatArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		float[][][][][] array = null;

		int[] size = new int[5];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new float[size[0]][size[1]][size[2]][size[3]][size[4]];



		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						for(int m = 0; m < array[0][0][0][0].length; m++) {

							value = 0;



							for(int n = 0; n < 8; n++)

								value |= (long)(file.read() & 0xff) << (8 * n);



							array[t][i][j][k][m] = (float)Double.longBitsToDouble(value);

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read5DFloatArray method





	// -------------------------------------------------------------------------------------

	// integer arrays

	// -------------------------------------------------------------------------------------



	/**

	 * Writes an array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Integers are converted to doubles.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(int[] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			// get the 64-bit long representation of the double

			value = Double.doubleToLongBits(array[i]);



			// write the 64-bit long to the stream

			for(int n = 0; n < 8; n++) {

				file.write((byte)(value & 0xff));



				value >>= 8;

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Integers are converted to doubles.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(int[][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				// get the 64-bit long representation of the double

				value = Double.doubleToLongBits(array[i][j]);



				// write the 64-bit long to the stream

				for(int n = 0; n < 8; n++) {

					file.write((byte)(value & 0xff));



					value >>= 8;

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Integers are converted to doubles.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(int[][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				for(int k = 0; k < array[0][0].length; k++) {

					// get the 64-bit long representation of the double

					value = Double.doubleToLongBits(array[i][j][k]);



					// write the 64-bit long to the stream

					for(int n = 0; n < 8; n++) {

						file.write((byte)(value & 0xff));



						value >>= 8;

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Integers are converted to doubles.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(int[][][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length, array[0][0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						// get the 64-bit long representation of the double

						value = Double.doubleToLongBits(array[t][i][j][k]);



						// write the 64-bit long to the stream

						for(int n = 0; n < 8; n++) {

							file.write((byte)(value & 0xff));



							value >>= 8;

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Integers are converted to doubles.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(int[][][][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length, array[0][0][0].length, array[0][0][0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						for(int m = 0; m < array[0][0][0][0].length; m++) {

							// get the 64-bit long representation of the double

							value = Double.doubleToLongBits(array[t][i][j][k][m]);



							// write the 64-bit long to the stream

							for(int n = 0; n < 8; n++) {

								file.write((byte)(value & 0xff));



								value >>= 8;

							} // end for

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method





	/**

	 * Reads an array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Double values in the file are converted to integers.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static int[] read1DIntArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		int[] array = null;

		int[] size = new int[1];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new int[size[0]];



		for(int i = 0; i < array.length; i++) {

			value = 0;



			for(int n = 0; n < 8; n++)

				value |= (long)(file.read() & 0xff) << (8 * n);



			array[i] = (int)Double.longBitsToDouble(value);



		} // end for



		file.close();



		return(array);

	} // end read1DIntArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Double values in the file are converted to integers.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static int[][] read2DIntArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		int[][] array = null;

		int[] size = new int[2];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new int[size[0]][size[1]];



		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				value = 0;



				for(int n = 0; n < 8; n++)

					value |= (long)(file.read() & 0xff) << (8 * n);



				array[i][j] = (int)Double.longBitsToDouble(value);



			} // end for

		} // end for



		file.close();



		return(array);

	} // end read2DIntArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Double values in the file are converted to integers.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static int[][][] read3DIntArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		int[][][] array = null;

		int[] size = new int[3];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new int[size[0]][size[1]][size[2]];



		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				for(int k = 0; k < array[0][0].length; k++) {

					value = 0;



					for(int n = 0; n < 8; n++)

						value |= (long)(file.read() & 0xff) << (8 * n);



					array[i][j][k] = (int)Double.longBitsToDouble(value);



				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read3DIntArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Double values in the file are converted to integers.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static int[][][][] read4DIntArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		int[][][][] array = null;

		int[] size = new int[4];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new int[size[0]][size[1]][size[2]][size[3]];



		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						value = 0;



						for(int n = 0; n < 8; n++)

							value |= (long)(file.read() & 0xff) << (8 * n);



						array[t][i][j][k] = (int)Double.longBitsToDouble(value);

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read4DIntArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Double values in the file are converted to integers.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static int[][][][][] read5DIntArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		int[][][][][] array = null;

		int[] size = new int[5];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new int[size[0]][size[1]][size[2]][size[3]][size[4]];



		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						for(int m = 0; m < array[0][0][0][0].length; m++) {

							value = 0;



							for(int n = 0; n < 8; n++)

								value |= (long)(file.read() & 0xff) << (8 * n);



							array[t][i][j][k][m] = (int)Double.longBitsToDouble(value);

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read5DIntArray method





	// -------------------------------------------------------------------------------------

	// boolean arrays

	// -------------------------------------------------------------------------------------



	/**

	 * Writes an array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Boolean "true" values are converted to 1.0 and "false" to 0.0.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(boolean[] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			// get the 64-bit long representation of the double

			value = Double.doubleToLongBits(array[i] ? 1 : 0);



			// write the 64-bit long to the stream

			for(int n = 0; n < 8; n++) {

				file.write((byte)(value & 0xff));



				value >>= 8;

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Boolean "true" values are converted to 1.0 and "false" to 0.0.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(boolean[][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				// get the 64-bit long representation of the double

				value = Double.doubleToLongBits(array[i][j] ? 1 : 0);



				// write the 64-bit long to the stream

				for(int n = 0; n < 8; n++) {

					file.write((byte)(value & 0xff));



					value >>= 8;

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Boolean "true" values are converted to 1.0 and "false" to 0.0.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(boolean[][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				for(int k = 0; k < array[0][0].length; k++) {

					// get the 64-bit long representation of the double

					value = Double.doubleToLongBits(array[i][j][k] ? 1 : 0);



					// write the 64-bit long to the stream

					for(int n = 0; n < 8; n++) {

						file.write((byte)(value & 0xff));



						value >>= 8;

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Boolean "true" values are converted to 1.0 and "false" to 0.0.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(boolean[][][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length, array[0][0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						// get the 64-bit long representation of the double

						value = Double.doubleToLongBits(array[t][i][j][k] ? 1 : 0);



						// write the 64-bit long to the stream

						for(int n = 0; n < 8; n++) {

							file.write((byte)(value & 0xff));



							value >>= 8;

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method



	/**

	 * Writes a rectangular jagged array to a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * Boolean "true" values are converted to 1.0 and "false" to 0.0.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * If the file already exists, then it will be overwritten.

	 *

	 * @param array the array to write to the file

	 * @param filename the name of the file to write to

	 * @throws IOException

	 */

	public static void write(boolean[][][][][] array, String filename) throws IOException {



		// the size of the array

		int[] size = {array.length, array[0].length, array[0][0].length, array[0][0][0].length, array[0][0][0][0].length};



		int temp;

		long value;



		// create a stream to the file, overwriting any existing file and creating

		// the necessary directories

		BufferedOutputStream file = getStream(filename);



		// write the number of array dimensions to the file

		temp = size.length;



		for(int i = 0; i < 4; i++) {

			file.write((byte)(temp & 0xff));



			temp >>= 8;

		}



		// write the array dimensions to the file

		for(int i = 0; i < size.length; i++) {



			temp = size[i];



			for(int j = 0; j < 4; j++) {

				file.write((byte)(temp & 0xff));



				temp >>= 8;

			}

		}



		// write the array to the file

		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						for(int m = 0; m < array[0][0][0][0].length; m++) {

							// get the 64-bit long representation of the double

							value = Double.doubleToLongBits(array[t][i][j][k][m] ? 1 : 0);



							// write the 64-bit long to the stream

							for(int n = 0; n < 8; n++) {

								file.write((byte)(value & 0xff));



								value >>= 8;

							} // end for

						} // end for

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();

	} // end write method





	/**

	 * Reads an array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static boolean[] read1DBooleanArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		boolean[] array = null;

		int[] size = new int[1];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new boolean[size[0]];



		for(int i = 0; i < array.length; i++) {

			value = 0;



			for(int n = 0; n < 8; n++)

				value |= (long)(file.read() & 0xff) << (8 * n);



			array[i] = (Double.longBitsToDouble(value) == 1);



		} // end for



		file.close();



		return(array);

	} // end read1DBooleanArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static boolean[][] read2DBooleanArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		boolean[][] array = null;

		int[] size = new int[2];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new boolean[size[0]][size[1]];



		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				value = 0;



				for(int n = 0; n < 8; n++)

					value |= (long)(file.read() & 0xff) << (8 * n);



				array[i][j] = (Double.longBitsToDouble(value) == 1);



			} // end for

		} // end for



		file.close();



		return(array);

	} // end read2DBooleanArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static boolean[][][] read3DBooleanArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		boolean[][][] array = null;

		int[] size = new int[3];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new boolean[size[0]][size[1]][size[2]];



		for(int i = 0; i < array.length; i++) {

			for(int j = 0; j < array[0].length; j++) {

				for(int k = 0; k < array[0][0].length; k++) {

					value = 0;



					for(int n = 0; n < 8; n++)

						value |= (long)(file.read() & 0xff) << (8 * n);



					array[i][j][k] = (Double.longBitsToDouble(value) == 1);



				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read3DBooleanArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * 1.0 values are converted to "true" and 0.0 to "false".

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static boolean[][][][] read4DBooleanArray(String filename) throws IOException {



		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		boolean[][][][] array = null;

		int[] size = new int[4];



		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);



		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");



		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new boolean[size[0]][size[1]][size[2]][size[3]];



		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						value = 0;



						for(int n = 0; n < 8; n++)

							value |= (long)(file.read() & 0xff) << (8 * n);



						array[t][i][j][k] = (Double.longBitsToDouble(value) == 1);

					} // end for

				} // end for

			} // end for

		} // end for



		file.close();



		return(array);

	} // end read4DBooleanArray method



	/**

	 * Reads a rectangular jagged array from a file.

	 *

	 * The first four bytes (an integer) in the file indicate the number of dimensions of the array.

	 * The size of the array along each of the dimensions are then written as four bytes each (integers),

	 * note that the size along each of the dimensions must remain constant

	 * The rest of the file contains the array values (double values)

	 *

	 * The 64-bit IEEE 754 floating-point "double format" bit layout is used

	 * to write the byte representation of double values to the file.

	 *

	 * 1.0 values are converted to "true" and 0.0 to "false".

	 *

	 * All values are written to the file in little endian form.

	 *

	 * @param filename the name of the file containing the array

	 * @return the array in the file

	 * @throws IOException

	 */

	public static boolean[][][][][] read5DBooleanArray(String filename) throws IOException {

	

		BufferedInputStream file = new BufferedInputStream(new FileInputStream(filename));



		boolean[][][][][] array = null;

		int[] size = new int[5];

		

		int temp;

		long value;



		// get the number of dimensions of the array in the file

		temp = (file.read() & 0xff)

					| ((file.read() & 0xff) << 8)

					| ((file.read() & 0xff) << 16)

					| ((file.read() & 0xff) << 24);

		

		if(temp != size.length)

			throw new IOException("File doesn't contain a " + size.length + " dimensional array");

		

		// get the size of the array along each of the dimensions

		for(int i = 0; i < size.length; i++)

			size[i] = (file.read() & 0xff)

					  | ((file.read() & 0xff) << 8)

					  | ((file.read() & 0xff) << 16)

					  | ((file.read() & 0xff) << 24);





		// read the array

		array = new boolean[size[0]][size[1]][size[2]][size[3]][size[4]];

		

		for(int t = 0; t < array.length; t++) {

			for(int i = 0; i < array[0].length; i++) {

				for(int j = 0; j < array[0][0].length; j++) {

					for(int k = 0; k < array[0][0][0].length; k++) {

						for(int m = 0; m < array[0][0][0][0].length; m++) {

							value = 0;

							

							for(int n = 0; n < 8; n++)

								value |= (long)(file.read() & 0xff) << (8 * n);

							

							array[t][i][j][k][m] = (Double.longBitsToDouble(value) == 1);

						} // end for

					} // end for	

				} // end for

			} // end for

		} // end for



		file.close();

		

		return(array);

	} // end read5DBooleanArray method

	

	

	/**

	 * Create a stream to the file specified by filename.

	 *

	 * If the file already exists, it is overwritten.  The necessary directories

	 * are created.

	 *

	 * @param filename the name of the file.

	 * @return a stream to the file.

	 */

	public static BufferedOutputStream getStream(String filename) throws IOException {

		File file = new File(filename);

			

		// create the necessary directories

		if (file.getParentFile() != null)

			file.getParentFile().mkdirs();

		

		// return a stream to the file

		return (new BufferedOutputStream(new FileOutputStream(filename)));

	}

} // end ArrayReaderWriter class
