/**
 * 
 */
package edu.emory.mathcs.nlp.bin;

import java.util.Collections;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.component.template.train.OnlineTrainer;
import edu.emory.mathcs.nlp.component.template.train.QAOnlineTrainer;
import edu.emory.mathcs.nlp.component.template.util.NLPMode;

/**
 * @author Amit_Deshmane
 *
 */
public class QANLPTrain
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	protected String configuration_file;
	@Option(name="-m", usage="output model file (optional)", required=false, metaVar="<filename>")
	protected String model_file = null;
	@Option(name="-p", usage="previously trained model file (optional)", required=false, metaVar="<filename>")
	protected String previous_model_file = null;
	@Option(name="-t", usage="training path (required)", required=true, metaVar="<filepath>")
	protected String train_path;
	@Option(name="-d", usage="development path (optional)", required=false, metaVar="<filepath>")
	protected String develop_path;
	@Option(name="-te", usage="training file extension (default: *)", required=false, metaVar="<string>")
	protected String train_ext = "*";
	@Option(name="-de", usage="development file extension (default: *)", required=false, metaVar="<string>")
	protected String develop_ext = "*";
	@Option(name="-mode", usage="mode (required: pos|ner|dep|srl|sent)", required=true, metaVar="<string>")
	protected String mode = null;
	
	public QANLPTrain(String[] args)
	{
		BinUtils.initArgs(args, this);
		List<String> trainFiles   = FileUtils.getFileList(train_path  , train_ext);
		List<String> developFiles = (develop_path != null) ? FileUtils.getFileList(develop_path, develop_ext) : null;
		OnlineTrainer<?> trainer = new QAOnlineTrainer<>();
		
		Collections.sort(trainFiles);
		if (developFiles != null) Collections.sort(developFiles);
		trainer.train(NLPMode.valueOf(mode), trainFiles, developFiles, configuration_file, model_file, previous_model_file);
	}
	
	static public void main(String[] args)
	{
		String [] arg = "-c config/config-train-qa.xml -t data/train -d data/dev -mode ner".split(" ");
		
		new QANLPTrain(arg);
	}
}