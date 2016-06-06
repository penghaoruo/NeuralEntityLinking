public class Main {
	
	public static void main(String[] args) throws Exception {
		EmbedFeature ef = new EmbedFeature();
		ef.loadEmbed();
		//ef.Serialize("wiki_embeddings.bin");
		ef.generate("/home/t-hapeng/data/toy/toy.x.bin", "test.txt");
	}
}
