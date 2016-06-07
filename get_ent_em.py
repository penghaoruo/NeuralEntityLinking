import sys
import gensim

model = gensim.models.Word2Vec.load_word2vec_format('/home/t-hapeng/data/embedding/vectors-knowledge-sg1000.bin', binary = True)
print('Done Loading Entity Embeddings!')

file_in = sys.argv[1]
file_out = sys.argv[2]
f_out = open(file_out, 'w')
with open(file_in, 'r') as f_in:
    for line in f_in:
        splitline = line.split()
	entity = splitline[0]
        id = splitline[1]
	if id in model.vocab: 
            f_out.write(entity)
            vec = model[id]
            strs = ["%.6f" % value for value in vec]
	    for str in strs:
                f_out.write('  ' + str)
            f_out.write('\n')
