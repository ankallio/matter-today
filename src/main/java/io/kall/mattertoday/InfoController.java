package io.kall.mattertoday;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;

@RestController
@Slf4j
public class InfoController {
	
	@Autowired
	private WebcalGuruService calService;
	
	@GetMapping("nimet")
	public List<String> nimipaivasankarit() throws IOException, ParserException {
		List<String> names = calService.getNimipaivaSankarit();
		return names;
	}
	
}
