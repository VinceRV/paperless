package space.paperless.pdfmeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PDFMetadataPDFBox implements PDFMetadata {

	private static final Logger LOG = LoggerFactory.getLogger(PDFMetadataPDFBox.class);
	private static final String SEP = "::";

	@Override
	public String readDescription(File fromFile) throws IOException {

		try (PDDocument document = PDDocument.load(fromFile)) {
			PDDocumentInformation pdd = document.getDocumentInformation();
			String parentFolderName = fromFile.getParentFile().getAbsolutePath().replace('\\', '/');
			String fileName = fromFile.getName();
			String description = parentFolderName + SEP + fileName + SEP + pdd.getKeywords() + SEP;

			if (StringUtils.isBlank(pdd.getKeywords())) {
				LOG.warn("Keywords of {} are blank", fromFile);
			}
			LOG.debug("Description of {} is {}", fromFile, description);

			return description;
		}
	}

	@Override
	public String[] readDescriptions(File root) throws IOException {
		PDFFilesVisitor pdfFilesVisitor = new PDFFilesVisitor();

		Files.walkFileTree(root.toPath(), pdfFilesVisitor);

		return pdfFilesVisitor.getDescriptions();
	}

	@Override
	public void writeDescription(File toFile, String description) throws IOException {

		try (PDDocument document = PDDocument.load(toFile)) {
			PDDocumentInformation pdd = document.getDocumentInformation();

			pdd.setKeywords(description);
			document.save(toFile);
		}
	}

	private class PDFFilesVisitor extends SimpleFileVisitor<Path> {
		private final LinkedList<String> descriptions = new LinkedList<>();

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			descriptions.add(readDescription(file.toFile()));
			return FileVisitResult.CONTINUE;
		}

		public String[] getDescriptions() {
			return descriptions.toArray(new String[0]);
		}
	}
}
