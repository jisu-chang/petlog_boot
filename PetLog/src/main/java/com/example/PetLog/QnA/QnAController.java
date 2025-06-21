package com.example.PetLog.QnA;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class QnAController {

    @Autowired
    QnAService qnaService;

    @GetMapping("/QnAInput")
    public String qnain(HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        return "QnA/QnAInput";
    }

    @PostMapping("/QnAInputSave")
    public String qnainsave(QnADTO qnADTO, HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        qnADTO.setQnaDate(LocalDate.now());
        qnADTO.setQnaStatus("처리중");
        qnADTO.setUserId(userId);
        qnADTO.setQnaAnswer(null);

        QnAEntity qnaEntity = qnADTO.entity();
        qnaService.insertQnA(qnaEntity);
        return "redirect:/QnAOut";
    }

    @GetMapping("/QnAOut")
    public String qnaout(Model mo, HttpSession session){
        Long userId = (Long) session.getAttribute("userId");

        List<QnAEntity> list = qnaService.allout();
        mo.addAttribute("list", list);
        return "QnA/QnAOut";
    }

    @GetMapping("/QnADetail")
    public String qnadetail(@RequestParam("qnaId") Long qnaId, HttpSession session, Model mo){
        Long userId = (Long) session.getAttribute("userId");

        QnAEntity qnAEntity=qnaService.DetailById(qnaId);
        mo.addAttribute("dto", qnAEntity);
        return "QnA/QnADetail";
    }

    @GetMapping("/QnAUpdate")
    public String qnaupdate(@RequestParam("qnaId") Long qnaId, HttpSession session, Model mo){
        Long userId = (Long) session.getAttribute("userId");

        QnAEntity qnAEntity=qnaService.UpdateById(qnaId);
        mo.addAttribute("dto", qnAEntity);
        return "QnA/QnAUpdate";
    }

    @PostMapping("/QnAUpdateSave")
    public String qnaupdatesave(QnADTO qnADTO ,HttpSession session){
        Long userId = (Long) session.getAttribute("userId");

        qnADTO.setQnaDate(LocalDate.now());
        qnADTO.setQnaStatus("처리중");
        qnADTO.setUserId(userId);
        qnADTO.setQnaAnswer(null);

        QnAEntity qnaEntity = qnADTO.entity();
        qnaService.UpdateSave(qnaEntity);
        return "redirect:/QnAOut";
    }

    @GetMapping("/QnADelete")
    public String qnadelete(@RequestParam("qnaId") Long qnaId, HttpSession session, Model mo){
        Long userId = (Long) session.getAttribute("userId");

        QnAEntity qnAEntity=qnaService.DeleteById(qnaId);
        mo.addAttribute("dto", qnAEntity);
        return "QnA/QnADelete";
    }

    @PostMapping("/QnADeleteSave")
    public String qnaudeletesave(@RequestParam("qnaId") Long qnaId){
        qnaService.DeleteSave(qnaId);
        return "redirect:/QnAOut";
    }

}