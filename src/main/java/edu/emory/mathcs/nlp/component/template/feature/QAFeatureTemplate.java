/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.component.template.feature;

import org.w3c.dom.Element;

import edu.emory.mathcs.nlp.component.qa.QAState;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.train.HyperParameter;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class QAFeatureTemplate extends FeatureTemplate<QAState>
{
	
	private static final long serialVersionUID = -4098254659680364723L;

	private static final int WESentLimit       = 40;
	
	public QAFeatureTemplate(Element eFeatures, HyperParameter hp) {
		super(eFeatures, hp);
	}

//	============================== DENSE FEATURES ==============================

	@Override
	public float[] getEmbeddingFeatures(QAState state)
	{
		if (GlobalLexica.word_embeddings == null || word_embeddings == null || word_embeddings.isEmpty()) return null;
		float[] w, v = null;
		NLPNode node;
		int i = -1;
		
		NLPNode[] qNodes = state.getQNodes();
		if(qNodes != null){
			for(int index = 0; index < WESentLimit && index < qNodes.length; index++){
				NLPNode qNode = qNodes[index];
				i++;
				if (qNode != null && qNode.hasWordEmbedding())
				{
					w = qNode.getWordEmbedding();
					if (v == null) v = new float[w.length * (word_embeddings.size() + WESentLimit)];
					System.arraycopy(w, 0, v, w.length*i, w.length);
				}
			}
		}
		for (FeatureItem item : word_embeddings)
		{
			node = state.getNode(item);
			i++;
			
			if (node != null && node.hasWordEmbedding())
			{
				w = node.getWordEmbedding();
				if (v == null) v = new float[w.length * (word_embeddings.size() + WESentLimit)];
				System.arraycopy(w, 0, v, w.length*i, w.length);
			}
		}
		
		return v;
	}
	
}
