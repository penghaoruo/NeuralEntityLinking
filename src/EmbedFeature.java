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
	public HashMap<String, Double[]> em = new HashMap<String, Double[]>();
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
			em.put(word, vec);
		}
		br.close();
		//outputVocab();
	}
	
	public void generate(String file_in, String file_out) throws IOException {
		//BufferedWriter bw = IOManager.openWriter(file_out);
		
		BufferedReader br = IOManager.openReader(file_in);
		String line = br.readLine().trim();
		while (line != null) {
			System.out.println(line);
			String strs[] = line.split("\t");
			int men_size = Integer.parseInt(strs[1]);
			//bw.write(line + "\n");
			
			for (int i = 0; i < men_size; i++) {
				line = br.readLine().trim();
				String men = line.toLowerCase();
				
				ArrayList<String> men_tokens = parseTokens(men);
				for (String token : men_tokens) {
					if (!mentions.contains(token)) {
						mentions.add(token);
					}
				}
				//Double[] vec_men = getVec(men);
				//bw.write(line + "\n");
				
				line = br.readLine().trim();
				int ent_size = Integer.parseInt(line.split("\t")[2]);
				//bw.write(line + "\n");
				
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
					//Double[] vec_ent = getVec(ent);
					//bw.write(line + "\n");
					
					line = br.readLine().trim();
					strs = line.split("  ");
//					String new_line = strs[0] + "  " + strs[1];
//					int index = 3;
//					for (int k = 0; k < vec_men.length; k++) {
//						new_line = new_line + " " + index + ":" + vec_men[k];
//						index++;
//					}
//					for (int k = 0; k < vec_ent.length; k++) {
//						new_line = new_line + " " + index + ":" + vec_ent[k];
//						index++;
//					}
					//bw.write(new_line + "\n");
				}
			}
			
			line = br.readLine();
			if (line != null) {
				line = line.trim();
			}
		}
		br.close();
		//bw.close();
		
		System.out.println("Done");
	}
	
	public void getMentionEmbeddings(String file_in, String file_out) throws Exception {
		ArrayList<String> words = IOManager.readLines(file_in);
		for (String word : words) {
			Double[] vec = em.get(word);
			if (vec == null) {
				System.out.println("Can't find token: " + word);
			} else {
				em_used.put(word, vec);
			}
		}
		System.out.println(words.size() + "\t" + em_used.size());
		Serialize(em_used, file_out);
	}
	
	public void outputVocab() throws IOException {
		BufferedWriter bw = IOManager.openWriter("vocab.txt");
		Map<String, Double[]> sorted = new TreeMap<String, Double[]>(em);
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
		em = (HashMap<String, Double[]>) in.readObject();
		System.out.println("Done reading the data from " + fileName);
		in.close();
		fileIn.close();
	}
	
	private Double[] getVec(String str) {
		ArrayList<String> tokens = parseTokens(str);
		
		Double[] vec = new Double[dim];
		for (int i = 0; i < dim; i++) {
			vec[i] = 0.0;
		}
		int count = 0;
		for (String token : tokens) {
			Double[] vec_add = em.get(token);
			if (vec_add == null) {
				System.out.println("Can't find token: " + token);
			} else {
				count++;
				vec = vectorAdd(vec, vec_add);
			}
		}
		vec = vectorAvg(vec, count);
		return vec;
	}

	private Double[] vectorAvg(Double[] vec, int count) {
		if (count == 0) {
			return vec;
		}
		for (int i = 0; i < dim; i++) {
			vec[i] = vec[i] / count;
		}
		return vec;
	}

	private Double[] vectorAdd(Double[] vec, Double[] vec_add) {
		for (int i = 0; i < dim; i++) {
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
