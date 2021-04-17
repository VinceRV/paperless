package space.paperless.domain;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;

import space.paperless.repository.IdUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Document implements Comparable<Document> {

	private static final Logger LOG = LoggerFactory.getLogger(Document.class);
	private static final Pattern FIELD_MATCHER = Pattern.compile("(\\w+)=\\{(.*?)\\}");
	private static final Pattern OLD_DESCRIPTION_MATCHER = Pattern.compile("(.+?):(.+?)(;|$)");
	private static final Pattern FILENAME_MATCHER = Pattern.compile("(20[12][0-9])([01][0-9]).*?_(.+)");

	private String documentId;
	private Map<String, Set<String>> descriptions = new LinkedHashMap<>();

	public Document() {
		super();
	}

	public Document(String documentId) {
		super();
		this.documentId = documentId;
		fixMandatoryDescriptionFields();
	}

	public Document(String documentId, String description) {
		this.documentId = documentId;

		fromDescription(description);
		fromOldDescription(description);
		fixMandatoryDescriptionFields();
	}

	public Document(String documentId, Map<String, Set<String>> descriptions) {
		this.documentId = documentId;
		this.descriptions = descriptions;
		fixMandatoryDescriptionFields();
	}

	public String toDescription() {
		StringBuilder keywords = new StringBuilder();

		for (Entry<String, Set<String>> description : descriptions.entrySet()) {
			if (keywords.length() != 0) {
				keywords.append(",");
			}

			if (description.getValue() != null && !description.getValue().isEmpty()) {
				for (String value : description.getValue()) {
					keywords.append(description.getKey()).append("={").append(value).append("}");
				}
			}
		}

		return keywords.toString();
	}

	public void fixMandatoryDescriptionFields() {
		String year = getFirstDescriptionValue(DescriptionType.YEAR);
		String month = getFirstDescriptionValue(DescriptionType.MONTH);
		String name = getFirstDescriptionValue(DescriptionType.NAME);

		if (StringUtils.isAnyBlank(year, month, name)) {
			String fileName = IdUtils.idToFileName(documentId);
			Matcher matcher = FILENAME_MATCHER.matcher(fileName);

			if (matcher.matches()) {
				fixMandatoryDescriptionFields(matcher);
			} else {
				defaultMandatoryDescriptionFields(fileName);
			}
		}
	}

	private void fixMandatoryDescriptionFields(Matcher matcher) {
		setDescriptionValueIfBlank(DescriptionType.YEAR, matcher.group(1));
		setDescriptionValueIfBlank(DescriptionType.MONTH, matcher.group(2));
		setDescriptionValueIfBlank(DescriptionType.NAME, matcher.group(3));
	}

	private void defaultMandatoryDescriptionFields(String fileName) {
		Calendar today = Calendar.getInstance();

		setDescriptionValueIfBlank(DescriptionType.YEAR, String.format("%1$tY", today));
		setDescriptionValueIfBlank(DescriptionType.MONTH, String.format("%1$tm", today));
		setDescriptionValueIfBlank(DescriptionType.NAME, fileName);
	}

	public String getDocumentId() {
		return documentId;
	}

	public Map<String, Set<String>> getDescriptions() {
		return descriptions;
	}

	public String getFirstDescriptionValue(DescriptionType descriptionType) {
		Set<String> values = descriptions.get(descriptionType.getName());

		if (values != null && !values.isEmpty()) {
			return values.iterator().next();
		}

		return null;
	}

	public Set<String> getDescriptionValues(DescriptionType descriptionType) {
		return descriptions.get(descriptionType.getName());
	}

	public void addDescriptionValue(DescriptionType descriptionType, String value) {
		Set<String> values = descriptions.computeIfAbsent(descriptionType.getName(), k -> new LinkedHashSet<>());

		values.add(value);
	}

	public void setDescriptionValue(DescriptionType descriptionType, String value) {
		Set<String> values = descriptions.get(descriptionType.getName());

		if (values == null) {
			values = new LinkedHashSet<>();
			descriptions.put(descriptionType.getName(), values);
		} else {
			values.clear();
		}

		values.add(value);
	}

	private void setDescriptionValueIfBlank(DescriptionType descriptionType, String newValue) {
		String oldValue = getFirstDescriptionValue(descriptionType);

		if (StringUtils.isBlank(oldValue)) {
			setDescriptionValue(descriptionType, newValue);
		}
	}

	private void fromDescription(String description) {
		Matcher fieldMatcher = FIELD_MATCHER.matcher(description);

		descriptions.clear();

		while (fieldMatcher.find()) {
			try {
				addDescriptionValue(DescriptionType.findByName(fieldMatcher.group(1)), fieldMatcher.group(2).trim());
			} catch (IllegalArgumentException e) {
				LOG.error("Unable to parse description " + description, e);
			}
		}
	}

	private void fromOldDescription(String description) {
		if (description == null) {
			return;
		}

		String[] parts = description.trim().split("::");

		if (parts.length > 2) {
			Matcher fieldMatcher = OLD_DESCRIPTION_MATCHER.matcher(parts[2]);

			while (fieldMatcher.find()) {
				try {
					addDescriptionValue(DescriptionType.findByName(fieldMatcher.group(1)),
							fieldMatcher.group(2).trim());
				} catch (IllegalArgumentException e) {
					LOG.error("Unable to parse description " + description, e);
				}
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentId == null) ? 0 : documentId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Document other = (Document) obj;
		if (documentId == null) {
			return other.documentId == null;
		} else return documentId.equals(other.documentId);
	}

	@Override
	public String toString() {
		return "Document [documentId=" + documentId + "]";
	}

	@Override
	public int compareTo(Document o) {
		return documentId.compareTo(o.documentId);
	}
}
