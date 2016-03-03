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
package edu.emory.mathcs.nlp.component.template.train;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.qa.QATagger;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.util.NLPMode;
import edu.emory.mathcs.nlp.component.template.util.TSVReader;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;

/**
 * Provide instances and methods for training NLP components.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class QAOnlineTrainer<S extends NLPState> extends OnlineTrainer<S>
{
	//<S extends NLPState>
	public QAOnlineTrainer() {};
	
	
//	=================================== COMPONENT ===================================

	@SuppressWarnings("unchecked")
	protected OnlineComponent<S> createComponent(NLPMode mode, InputStream config)
	{
		switch (mode)
		{
		case ner : return (OnlineComponent<S>)new QATagger(config);
		default : throw new IllegalArgumentException("Unsupported mode: "+mode);
		}
	}
	
//	=================================== HELPERS ===================================

	@Override
	protected double iterate(TSVReader reader, List<String> inputFiles, Consumer<NLPNode[]> f, OnlineOptimizer optimizer, HyperParameter info)
	{
		NLPNode[] nodes;
		int count = 0;
		long st, et, ttime = 0, tnode = 0;
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			try
			{
				while ((nodes = reader.next()) != null)
				{
					NLPNode [] qNodes = nodes;
					NLPNode [] aNodes = reader.next();
					
					GlobalLexica.assignGlobalLexica(qNodes);
					GlobalLexica.assignGlobalLexica(aNodes);
					
					NLPNode [] total = new NLPNode[qNodes.length + 1 + aNodes.length];
					
					int index = 0;
					for(index = 0; index < qNodes.length; index++){
						total[index] = qNodes[index];
					}
					total[index] = null;
					for(index = index + 1; index < qNodes.length + 1 + aNodes.length; index++){
						total[index] = aNodes[index - 1 - qNodes.length];
					}
					
					st = System.currentTimeMillis();
					f.accept(total);
					et = System.currentTimeMillis();
					ttime += et - st;
					tnode += aNodes.length - 1;
					count = update(optimizer, info, count, false);
				}
			}
			catch (IOException e) {e.printStackTrace();}
			reader.close();
		}
		
		update(optimizer, info, count, true);
		return 1000d * tnode / ttime;
	}
	
}
