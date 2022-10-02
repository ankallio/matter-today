package io.kall.mattertoday;

import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

@Service
@Slf4j
public class WebcalGuruService {
	
	@Autowired
	private WebcalGuruClient webcalGuru;
	
	@Autowired
	private ZoneId zone;
	
	private LocalDate todayDate() {
		return LocalDate.now(zone);
	}
	
	public List<String> getNimipaivaSankarit() throws IOException, ParserException {
		Instant startTime = Instant.now();
		String ical = webcalGuru.getNimipaivatIcal();
		log.info("Nimipäivä lookup took: {}", Duration.between(startTime, Instant.now()));
		
		Stream<VEvent> events = todayEvents(ical);
		
		return events.flatMap(event -> {
			return Optional.ofNullable(event.getSummary())
					.map(s -> s.getValue())
					.map(names -> StringUtils.splitByWholeSeparator(names, ", "))
					.map(List::of)
					.map(list -> list.stream())
					.orElse(Stream.empty());
		}).sorted().collect(Collectors.toList());
	}
	
	public Stream<String> getHyvaTietaa() throws IOException, ParserException {
		Instant startTime = Instant.now();
		String ical = webcalGuru.getHyvaTietaa();
		log.info("HyvaTietaa lookup took: {}", Duration.between(startTime, Instant.now()));
		
		return getValueUrlMarkdown(ical);
	}
	
	public Stream<String> getPyhat() throws IOException, ParserException {
		Instant startTime = Instant.now();
		String ical = webcalGuru.getPyhat();
		log.info("Pyhät lookup took: {}", Duration.between(startTime, Instant.now()));
		
		return getValueUrlMarkdown(ical);
	}
	
	public Stream<String> getHauskatMerkkipaivat() throws IOException, ParserException {
		Instant startTime = Instant.now();
		String ical = webcalGuru.getHauskatMerkkipaivat();
		log.info("HauskatMerkkipäivät lookup took: {}", Duration.between(startTime, Instant.now()));
		
		return getValueUrlMarkdown(ical);
	}
	
	public Stream<String> getValueUrlMarkdown(String ical) throws IOException, ParserException {
		return todayEvents(ical).map(event -> {
			String summary = Optional.ofNullable(event.getSummary()).map(s -> s.getValue()).orElse(null);
			if (summary == null)
				return null;
			String description = Optional.ofNullable(event.getDescription()).map(d -> d.getValue()).map(s -> s.replace("\"", "'")).orElse(null);
			// TODO how to include description text
			String url = Optional.ofNullable(event.getUrl()).map(u -> u.getValue()).orElse(null);
			if (url != null)
				return String.format("[%s](%s)", summary, url);
			else
				return summary;
		});
	}
	
	
	
	private Stream<VEvent> todayEvents(String icalData) throws IOException, ParserException {
		Calendar calendar = new CalendarBuilder().build(new StringReader(icalData));
		
		return calendar.getComponents().stream().filter(c -> c instanceof VEvent).map(c -> (VEvent)c)
		// Events for today
		.filter(event -> {
			return Optional.ofNullable(event.getStartDate())
					.map(sd -> sd.getDate())
					.map(date -> LocalDate.of(date.getYear()+1900, date.getMonth()+1, date.getDate()))
					.map(ld -> ld.equals(todayDate()))
					.orElse(false);
		});
	}
	
}
