import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EmbedFeature {
	public String em_file = "/home/t-hapeng/data/embedding/vectors-enwikitext_vivek200.txt";
	public int dim = 200;
	public int vocab_size = 3616574;
	public HashMap<String, Double[]> men_em = new HashMap<String, Double[]>();
	public HashMap<String, Double[]> ent_em = new HashMap<String, Double[]>();
	public ArrayList<String> mentions = new ArrayList<String>();
	public ArrayList<String> entities = new ArrayList<String>();
	public HashMap<String, Double[]> em_used = new HashMap<String, Double[]>();
	
	public void loadEmbed() throws IOException {
		BufferedReader br = IOManager.openReader(em_file);
		String line = br.readLine();
		String strs[] = line.split(" ");
		vocab_size = Integer.parseInt(strs[0]);
		dim = Integer.parseInt(strs[1]);
		
		for (int i = 0; i < vocab_size; i++) {
			if (i % 10000 == 0) {
				System.out.print(i + " ");
			}
			line = br.readLine();
			String tokens[] = line.split(" ");
			String word = tokens[0];
			Double[] vec = new Double[dim];
			for (int j = 1; j < dim + 1; j++) {
				vec[j-1] = Double.parseDouble(tokens[j]);
			}
			men_em.put(word, vec);
		}
		br.close();
	}
	
	public void loadEntEmbed(String file, int ent_em_dim) throws IOException {
		BufferedReader br = IOManager.openReader(file);
		String line = br.readLine();
		
		while (line != null) {
			String strs[] = line.split("  ");
			String ent = strs[0];
			Double[] vec = new Double[ent_em_dim];
			for (int i = 1; i < ent_em_dim + 1; i++) {
				vec[i-1] = Double.parseDouble(strs[i]);
			}
			ent_em.put(ent, vec);
			line = br.readLine();
		}
		br.close();
		System.out.println("Done Reading from " + file);
	}
	
	public void getTokens(String file_in) throws IOException {
		BufferedReader br = IOManager.openReader(file_in);
		String line = br.readLine().trim();
		while (line != null) {
			System.out.println(line);
			String strs[] = line.split("\t");
			int men_size = Integer.parseInt(strs[1]);
			
			for (int i = 0; i < men_size; i++) {
				line = br.readLine().trim();
				String men = line.toLowerCase();
				
				ArrayList<String> men_tokens = parseTokens(men);
				for (String token : men_tokens) {
					if (!mentions.contains(token)) {
						mentions.add(token);
					}
				}
				
				line = br.readLine().trim();
				int ent_size = Integer.parseInt(line.split("\t")[2]);
				
				for (int j = 0; j < ent_size; j++) {
					line = br.readLine().trim();
					String ent = line.toLowerCase();
					ArrayList<String> ent_tokens = parseTokens(ent);
					for (String token : ent_tokens) {
						if (!mentions.contains(token)) {
							mentions.add(token);
						}
					}
					if (!entities.contains(line)) {
						entities.add(line);
					}
					
					line = br.readLine().trim();
					strs = line.split("  ");
				}
			}
			
			line = br.readLine();
			if (line != null) {
				line = line.trim();
			}
		}
		br.close();
		
		System.out.println("Done");
	}
	
	public void generate(String file_in, String file_out) throws IOException {
		BufferedWriter bw = IOManager.openWriter(file_out);
		
		BufferedReader br = IOManager.openReader(file_in);
		String line = br.readLine().trim();
		while (line != null) {
			//System.out.println(line);
			String strs[] = line.split("\t");
			int men_size = Integer.parseInt(strs[1]);
			bw.write(line + "\n");
			
			for (int i = 0; i < men_size; i++) {
				line = br.readLine().trim();
				String men = line.toLowerCase();
				Double[] vec_men = getMenVec(men, 200);
				bw.write(line + "\n");
				
				line = br.readLine().trim();
				int ent_size = Integer.parseInt(line.split("\t")[2]);
				bw.write(line + "\n");
				
				for (int j = 0; j < ent_size; j++) {
					line = br.readLine().trim();
					String ent = line;
					Double[] vec_ent = getEntVec(ent, 1000);
					bw.write(line + "\n");
					
					line = br.readLine().trim();
					strs = line.split("  ");
					String new_line = strs[0] + "  " + strs[1];//line;
					int index = 3; //41;
					for (int k = 0; k < vec_men.length; k++) {
						new_line = new_line + " " + index + ":" + vec_men[k];
						index++;
					}
					for (int k = 0; k < vec_ent.length; k++) {
						new_line = new_line + " " + index + ":" + vec_ent[k];
						index++;
					}
					bw.write(new_line + "\n");
				}
			}
			
			line = br.readLine();
			if (line != null) {
				line = line.trim();
			}
		}
		br.close();
		bw.close();
		
		System.out.println("Done");
	}
	
	public void getMentionEmbeddings(String file_in, String file_out) throws Exception {
		ArrayList<String> words = IOManager.readLines(file_in);
		for (String word : words) {
			Double[] vec = men_em.get(word);
			if (vec == null) {
				System.out.println("Can't find token: " + word);
			} else {
				em_used.put(word, vec);
			}
		}
		System.out.println(words.size() + "\t" + em_used.size());
		Serialize(em_used, file_out);
	}
	
	public void getEntitiyIDs(String map_in, String file_in, String file_out) throws Exception {
		ArrayList<String> lines = IOManager.readLines(map_in);
		HashMap<String, String> id_map = new HashMap<String, String>();
		for (String line : lines) {
			String[] strs = line.split("\t");
			id_map.put(strs[0], strs[2]);
		}
		
		BufferedWriter bw = IOManager.openWriter(file_out);
		ArrayList<String> ents = IOManager.readLines(file_in);
		for (String ent : ents) {
			String id = id_map.get(ent);
			if (id != null) {
				bw.write(ent + "\t" + id +"\n");
			}
		}
		bw.close();
	}
	
	public void getEntityFromIDs(String map_in, String file_in, String file_out) throws IOException {
		ArrayList<String> lines = IOManager.readLines(map_in);
		HashMap<String, String> id_map = new HashMap<String, String>();
		for (String line : lines) {
			String[] strs = line.split("\t");
			id_map.put(strs[2], strs[0]);
		}
		
		BufferedWriter bw = IOManager.openWriter(file_out);
		lines = IOManager.readLines(file_in);
		for (String line : lines) {
			int p = line.indexOf('/');
			int q = line.indexOf('\t');
			String id = line.substring(p, q);
			String ent = id_map.get(id);
			if (ent != null) {
				bw.write(ent + "\n");
			} else {
				System.out.println(id);
				bw.write("Not Found!\n");
			}
		}
		bw.close();
	}
	
	public void outputVocab() throws IOException {
		BufferedWriter bw = IOManager.openWriter("vocab.txt");
		Map<String, Double[]> sorted = new TreeMap<String, Double[]>(men_em);
		for (String word : sorted.keySet()) {
			bw.write(word + "\n");
		}
		bw.close();
	}
	
	public void getWordList(String file) throws IOException {
		BufferedWriter bw1 = IOManager.openWriter(file + "_men.txt");
		for (String str : mentions) {
			bw1.write(str + "\n");
		}
		bw1.close();
		
		BufferedWriter bw2 = IOManager.openWriter(file + "_ent.txt");
		for (String str : entities) {
			bw2.write(str + "\n");
		}
		bw2.close();
	}
	
	public void Serialize(HashMap<String, Double[]> obj, String fileName) throws Exception {
		FileOutputStream fileOut = new FileOutputStream(fileName);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(obj);
		System.out.println("Done writing the data to " + fileName);
		out.close();
		fileOut.close();
	}
	
	public void Deserialize(String fileName) throws Exception {
		FileInputStream fileIn = new FileInputStream(fileName);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		men_em = (HashMap<String, Double[]>) in.readObject();
		System.out.println("Done reading the data from " + fileName);
		in.close();
		fileIn.close();
	}
	
	private Double[] getMenVec(String str, int cur_dim) {
		ArrayList<String> tokens = parseTokens(str);
		
		Double[] vec = new Double[cur_dim];
		for (int i = 0; i < cur_dim; i++) {
			vec[i] = 0.0;
		}
		int count = 0;
		for (String token : tokens) {
			Double[] vec_add = men_em.get(token);
			if (vec_add == null) {
				//System.out.println("Can't find token: " + token);
			} else {
				count++;
				vec = vectorAdd(vec, vec_add);
			}
		}
		vec = vectorAvg(vec, count);
		return vec;
	}
	
	private Double[] getEntVec(String str, int cur_dim) {
		Double[] vec = ent_em.get(str);
		if (vec == null) {
			//System.out.println("Can't find Entity: " + str);
			vec = new Double[cur_dim];
			for (int i = 0; i < cur_dim; i++) {
				vec[i] = 0.0;
			}
		} else {
			if (vec.length != cur_dim) {
				System.out.println("Dimension Wrong: " + str + " " + vec.length);
				System.exit(-1);
			}
		}
		return vec;
	}

	private Double[] vectorAvg(Double[] vec, int count) {
		if (count == 0) {
			return vec;
		}
		for (int i = 0; i < vec.length; i++) {
			vec[i] = vec[i] / count;
		}
		return vec;
	}

	private Double[] vectorAdd(Double[] vec, Double[] vec_add) {
		for (int i = 0; i < vec.length; i++) {
			vec[i] += vec_add[i];
		}
		return vec;
	}

	private ArrayList<String> parseTokens(String str) {
		ArrayList<String> tokens = new ArrayList<String>();
		str = str + "#";
		int p = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) < 'a' || str.charAt(i) > 'z' ) {
				if (i > p) {
					String s = str.substring(p, i);
					tokens.add(s);
				}
				p = i + 1;
			}
		}
		return tokens;
	}
}
