package com.marklogic.hello.spring.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.marklogic.hello.Entry;
import com.marklogic.hello.KnowledgeBase;

/**
 * Handles requests for the application home page.
 */
@Controller
public class EntriesController {

    // private static final Logger logger =
    // LoggerFactory.getLogger(HomeController.class);

    private KnowledgeBase kb;

    @Autowired
    public EntriesController(KnowledgeBase kb) {
        this.kb = kb;
    }

    @RequestMapping(value = "/entries/new", method = RequestMethod.GET)
    public String newEntry(Model model) {
        return "entry";
    }

    @RequestMapping(value = "/entries", method = RequestMethod.POST)
    public String addEntry(Model model, @RequestParam(value = "entryData") String entryData) {
        Entry entry = new Entry();
        // TODO: Map inputs to Entry instance.
        kb.storeEntry(entry);
        return "redirect:/entries/" + entry.getId();
    }

    @RequestMapping(value = "/entries", method = RequestMethod.GET)
    public String listEntries(Model model, @RequestParam(value = "q", defaultValue = "") String query) {
        List<Entry> entries = kb.findEntries(query);
        model.addAttribute("entries", entries);
        model.addAttribute("query", query);
        return "entries"; // view name
    }

}
