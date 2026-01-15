package com.cg.objreader;

import com.cg.math.Vector2f;
import com.cg.math.Vector3f;
import com.cg.model.Model;
import com.cg.model.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ObjReader {

	private static final String OBJ_VERTEX_TOKEN = "v";
	private static final String OBJ_TEXTURE_TOKEN = "vt";
	private static final String OBJ_NORMAL_TOKEN = "vn";
	private static final String OBJ_FACE_TOKEN = "f";

	public static Model read(String fileContent) {
		Model result = new Model();

		int lineInd = 0;
		Scanner scanner = new Scanner(fileContent);
		while (scanner.hasNextLine()) {
			final String line = scanner.nextLine();
			ArrayList<String> wordsInLine = new ArrayList<>(Arrays.asList(line.split("\\s+")));
			if (wordsInLine.isEmpty() || wordsInLine.get(0).isEmpty()) {
				continue;
			}

			final String token = wordsInLine.get(0);
			wordsInLine.remove(0);

			++lineInd;
			switch (token) {
				case OBJ_VERTEX_TOKEN -> result.vertices.add(parseVertex(wordsInLine, lineInd));
				case OBJ_TEXTURE_TOKEN -> result.textureVertices.add(parseTextureVertex(wordsInLine, lineInd));
				case OBJ_NORMAL_TOKEN -> result.normals.add(parseNormal(wordsInLine, lineInd));
				case OBJ_FACE_TOKEN -> result.polygons.add(parseFace(wordsInLine, lineInd));
				default -> {}
			}
		}

		return result;
	}

	public static Vector3f parseVertex(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
		try {
			return new Vector3f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)),
					Float.parseFloat(wordsInLineWithoutToken.get(2)));
		} catch(NumberFormatException e) {
			throw new ObjReaderException("Failed to parse float value.", lineInd);
		} catch(IndexOutOfBoundsException e) {
			throw new ObjReaderException("Too few vertex arguments.", lineInd);
		}
	}

	protected static Vector2f parseTextureVertex(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
		try {
			return new Vector2f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)));
		} catch(NumberFormatException e) {
			throw new ObjReaderException("Failed to parse float value.", lineInd);
		} catch(IndexOutOfBoundsException e) {
			throw new ObjReaderException("Too few texture vertex arguments.", lineInd);
		}
	}

	protected static Vector3f parseNormal(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
		try {
			return new Vector3f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)),
					Float.parseFloat(wordsInLineWithoutToken.get(2)));
		} catch(NumberFormatException e) {
			throw new ObjReaderException("Failed to parse float value.", lineInd);
		} catch(IndexOutOfBoundsException e) {
			throw new ObjReaderException("Too few normal arguments.", lineInd);
		}
	}

	public static Polygon parseFace(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
		ArrayList<Integer> onePolygonVertexIndices = new ArrayList<>();
		ArrayList<Integer> onePolygonTextureVertexIndices = new ArrayList<>();
		ArrayList<Integer> onePolygonNormalIndices = new ArrayList<>();

		for (String s : wordsInLineWithoutToken) {
			parseFaceWord(s, onePolygonVertexIndices, onePolygonTextureVertexIndices, onePolygonNormalIndices, lineInd);
		}

		Polygon result = new Polygon();
		result.setVertexIndices(onePolygonVertexIndices);
		result.setTextureVertexIndices(onePolygonTextureVertexIndices);
		result.setNormalIndices(onePolygonNormalIndices);
		return result;
	}

	protected static void parseFaceWord(
			String wordInLine,
			ArrayList<Integer> vIndices,
			ArrayList<Integer> tvIndices,
			ArrayList<Integer> nIndices,
			int lineInd) {
		try {
			String[] wordIndices = wordInLine.split("/");
			switch (wordIndices.length) {
				case 1 -> { // f v1 v2 v3
					vIndices.add(Integer.parseInt(wordIndices[0]) - 1);
				}
				case 2 -> { // f v1/vt1 v2/vt2 v3/vt3
					vIndices.add(Integer.parseInt(wordIndices[0]) - 1);
					tvIndices.add(Integer.parseInt(wordIndices[1]) - 1);
				}
				case 3 -> {
					vIndices.add(Integer.parseInt(wordIndices[0]) - 1);
					// Случай f v1//vn1 (пропуск текстуры)
					if (!wordIndices[1].isEmpty()) {
						tvIndices.add(Integer.parseInt(wordIndices[1]) - 1);
					}
					if (!wordIndices[2].isEmpty()) {
						nIndices.add(Integer.parseInt(wordIndices[2]) - 1);
					}
				}
				default -> throw new ObjReaderException("Invalid face element size.", lineInd);
			}
		} catch(NumberFormatException e) {
			throw new ObjReaderException("Failed to parse int value.", lineInd);
		} catch(IndexOutOfBoundsException e) {
			throw new ObjReaderException("Too few arguments in face.", lineInd);
		}
	}
}