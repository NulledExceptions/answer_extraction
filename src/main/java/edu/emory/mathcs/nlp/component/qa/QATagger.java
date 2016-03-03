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
package edu.emory.mathcs.nlp.component.qa;

import java.io.InputStream;

import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.eval.F1Eval;
import edu.emory.mathcs.nlp.component.template.feature.QAFeatureTemplate;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author amit-deshmane (amitad87@gmail.com)
 */
public class QATagger extends OnlineComponent<QAState>
{

	private static final long serialVersionUID = -7513713923776523281L;

	public QATagger() {}
	
	public QATagger(InputStream configuration)
	{
		super(configuration);
	}
	
//	============================== GETTERS/SETTERS ==============================

	/** {@link #config} and {@link #hyper_parameter} must not be null. */
	@Override
	public void initFeatureTemplate()
	{
		feature_template = new QAFeatureTemplate(config.getFeatureTemplateElement(), getHyperParameter());
	}
	
//	============================== ABSTRACT ==============================
	
	@Override
	public Eval createEvaluator()
	{
		return new F1Eval();
	}
	
	@Override
	protected QAState initState(NLPNode[] nodes)
	{
		int index = 0;
		for(index = 0; index < nodes.length; index++){
			if(nodes[index] == null){
				break;
			}
		}
		NLPNode[] qNodes = new NLPNode[index];
		NLPNode[] aNodes = new NLPNode[nodes.length - index - 1];
		
		for(int i = 0; i < index; i++){
			qNodes[i] = nodes[i];
		}
		for(int i = 0; i < aNodes.length; i++){
			aNodes[i] = nodes[index + i + 1];
		}
		return new QAState(qNodes, aNodes);
	}
	
	@Override
	protected void postProcess(QAState state)
	{
		state.postProcess();
	}
	
}
