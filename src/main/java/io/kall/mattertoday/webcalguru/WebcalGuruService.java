package io.kall.mattertoday.webcalguru;

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

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

@ApplicationScoped
public class WebcalGuruService {
	
	private static final Logger log = LoggerFactory.getLogger(WebcalGuruService.class);
	
	@RestClient
	WebcalGuruClient webcalGuru;
	
	private static final ZoneId ZONE = ZoneId.systemDefault();
	
	public List<String> getNimipaivaSankarit() throws IOException, ParserException {
		// https://www.webcal.guru/fi-FI/tapahtumalista?calendar_id=name_days_finland
		// https://www.webcal.guru/fi-FI/lataa_kalenteri?calendar_instance_id=263
		Instant startTime = Instant.now();
		String ical = webcalGuru.getIcal("263");
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
//		// https://www.webcal.guru/fi-FI/tapahtumalista?calendar_id=holidays_good_to_know
//		// https://www.webcal.guru/fi-FI/lataa_kalenteri?calendar_instance_id=180
		Instant startTime = Instant.now();
		String ical = webcalGuru.getIcal("180");
		log.info("HyvaTietaa lookup took: {}", Duration.between(startTime, Instant.now()));
		
		return getValueUrlMarkdown(ical);
	}
	
	public Stream<String> getPyhat() throws IOException, ParserException {
//		// https://www.webcal.guru/fi-FI/tapahtumalista?calendar_id=holidays
//		// https://www.webcal.guru/fi-FI/lataa_kalenteri?calendar_instance_id=52
		Instant startTime = Instant.now();
		String ical = webcalGuru.getIcal("52");
		log.info("Pyhät lookup took: {}", Duration.between(startTime, Instant.now()));
		
		return getValueUrlMarkdown(ical);
	}
	
	public Stream<String> getHauskatMerkkipaivat() throws IOException, ParserException {
//		// https://www.webcal.guru/fi-FI/tapahtumalista?calendar_id=holidays_funny_local
//		// https://www.webcal.guru/fi-FI/lataa_kalenteri?calendar_instance_id=3082
		Instant startTime = Instant.now();
		String ical = webcalGuru.getIcal("3082");
		log.info("HauskatMerkkipäivät lookup took: {}", Duration.between(startTime, Instant.now()));
		
		return getValueUrlMarkdown(ical);
	}
	
	private Stream<String> getValueUrlMarkdown(String ical) throws IOException, ParserException {
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
	
	private LocalDate todayDate() {
		return LocalDate.now(ZONE);
	}
}
