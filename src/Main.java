public class Main {
	public static String men_em_file = "standard_men_em.bin";
	public static String ent_em_file = "standard_ent_em.txt";
	
	public static void main(String[] args) throws Exception {
		EmbedFeature ef = new EmbedFeature();
		
		//ef.getTokens("/home/t-hapeng/data/toy/bin_output-trainDev.x.bin");
		//ef.getTokens("/home/t-hapeng/data/toy/bin_output-test.x.bin");
		//ef.getTokens("/home/t-hapeng/data/toy/bin_output-tacl.x.bin");
		//ef.getWordList("standard");
		//ef.getWordList("synthetic");
		
		//ef.loadEmbed();
		//ef.getMentionEmbeddings("standard_men.txt", "standard_men_em.bin");
		
		//ef.getEntitiyIDs("/home/t-hapeng/data/embedding/wikifbmap.txt", "standard_label.txt", "standard_label_ids.txt");
		// ./getEntEm.sh
		
		ef.Deserialize(men_em_file);
		ef.loadEntEmbed(ent_em_file, 1000);
		ef.generate("/home/t-hapeng/data/toy/bin_output-trainDev.x.bin", "trainDev_with_em.x.bin");
		ef.generate("/home/t-hapeng/data/toy/bin_output-test.x.bin", "test_with_em.x.bin");
		ef.generate("/home/t-hapeng/data/toy/bin_output-tacl.x.bin", "tacl_with_em.x.bin");
		
		//ef.getEntityFromIDs("/home/t-hapeng/data/embedding/wikifbmap.txt", "/home/t-hapeng/data/fbdata/annotated_fb_data_test.txt", "fbdata_test_label.txt");
		//ef.getEntityFromIDs("/home/t-hapeng/data/embedding/wikifbmap.txt", "/home/t-hapeng/data/fbdata/annotated_fb_data_valid.txt", "fbdata_vaild_label.txt");
		//ef.getEntityFromIDs("/home/t-hapeng/data/embedding/wikifbmap.txt", "/home/t-hapeng/data/fbdata/annotated_fb_data_train.txt", "fbdata_train_label.txt");
	}
}
