package cn.odc.shell.replacer;

import cn.odc.shell.util.FileUtils;
import cn.odc.shell.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class ReplacementProcessor {

	public void replace(List<Replacement> replacements, String sourceFile,
			String outputFile) throws IOException {
		InputStream inputStream = Replacement.class
				.getResourceAsStream(sourceFile);
		String content = IOUtils.toString(inputStream, "UTF-8");
		for (Replacement replacement : replacements) {
			content = replaceContent(content, replacement);
		}
		FileUtils.writeToFile(outputFile, content, "UTF-8");
	}

	private String replaceContent(String content, Replacement replacement) {
		if (StringUtil.isEmpty(replacement.getToken())) {
			throw new IllegalArgumentException("Token or token file required");
		}
		return StringUtil.replace(content, replacement);
	}
}
