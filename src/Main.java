public class Main {
	
	public static void main(String[] args) throws Exception {
		EmbedFeature ef = new EmbedFeature();
		
		//ef.getTokens("/home/t-hapeng/data/toy/bin_output-trainDev.x.bin", "trainDev_with_em.x.bin");
		//ef.getTokens("/home/t-hapeng/data/toy/bin_output-test.x.bin", "test_with_em.x.bin");
		//ef.getTokens("/home/t-hapeng/data/toy/bin_output-tacl.x.bin", "tacl_with_em.x.bin");
		//ef.getWordList("standard");
		//ef.getWordList("synthetic");
		
		//ef.loadEmbed();
		//ef.getMentionEmbeddings("standard_men.txt", "standard_men_em.bin");
		
		ef.getEntitiyIDs("/home/t-hapeng/data/embedding/wikifbmap.txt", "standard_ent.txt", "standard_ent_ids.txt");
		// python 
	}
}
