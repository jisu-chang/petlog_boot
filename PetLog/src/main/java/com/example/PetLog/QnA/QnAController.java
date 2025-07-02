package com.example.PetLog.QnA;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        qnADTO.setQnaStatus("접수");
        qnADTO.setUserId(userId);
        qnADTO.setQnaAnswer(null);

        QnAEntity qnaEntity = qnADTO.entity();
        qnaService.insertQnA(qnaEntity);
        return "redirect:/QnAOut";
    }

    @GetMapping("/QnAOut")
    public String qnaout(@RequestParam(defaultValue = "1") int page, Model mo, HttpSession session) {
        int pageSize = 5;
        int offset = (page - 1) * pageSize;

        Long userId = (Long) session.getAttribute("userId");
        List<QnAEntity> list = qnaService.getQnAPage(offset, pageSize);
        int totalCount = qnaService.getTotalQnACount();

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        mo.addAttribute("list", list);
        mo.addAttribute("currentPage", page);
        mo.addAttribute("totalPages", totalPages);

        return "QnA/QnAOut";
    }

    @GetMapping("/QnADetail")
    public String qnadetail(@RequestParam("qnaId") Long qnaId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            Model mo) {
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole"); // 관리자 여부 확인

        QnAEntity qnAEntity = qnaService.DetailById(qnaId);

        // 작성자 본인이 아니면서 관리자가 아닌 경우 차단
        if (!qnAEntity.getUserId().equals(userId)) {
            if (userRole == null || !userRole.equals("admin")) {
                redirectAttributes.addFlashAttribute("alertMsg", "본인만 해당 게시글을 볼 수 있습니다.");
                return "redirect:/QnAOut";
            }
        }

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
    public String qnaupdatesave(QnADTO qnADTO,@RequestParam("qnaId") Long qnaId ,HttpSession session){
        Long userId = (Long) session.getAttribute("userId");

        qnADTO.setQnaDate(LocalDate.now());
        qnADTO.setQnaStatus("접수");
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

    @PostMapping("/updateAnswer")
    public String updateAnswer(@RequestParam("qnaId") Long qnaId,
                               @RequestParam("qnaAnswer") String qnaAnswer,
                               @RequestParam("qnaStatus") String qnaStatus,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        // 세션에서 관리자 확인 (선택)
        String role = (String) session.getAttribute("userRole");
        if (role == null || !role.equals("admin")) {
            redirectAttributes.addFlashAttribute("alertMsg", "접근 권한이 없습니다.");
            return "redirect:/QnADetail?qnaId=" + qnaId;
        }

        // 기존 QnA 불러오기
        QnAEntity qnaEntity = qnaService.DetailById(qnaId);
        qnaEntity.setQnaAnswer(qnaAnswer);
        qnaEntity.setQnaStatus(qnaStatus);

        // 저장
        qnaService.UpdateSave(qnaEntity);

        redirectAttributes.addFlashAttribute("alertMsg", "답변이 등록되었습니다.");
        return "redirect:/QnADetail?qnaId=" + qnaId;
    }

    @GetMapping("/UserQnAOut")
    public String userqnaout(@RequestParam(defaultValue = "1") int page, Model mo, HttpSession session) {
        int pageSize = 5;
        int offset = (page - 1) * pageSize;

        Long userId = (Long) session.getAttribute("userId");

        List<QnAEntity> list = qnaService.getUserQnAPage(userId, offset, pageSize);
        int totalCount = qnaService.getTotalUserQnACount(userId);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        mo.addAttribute("list", list);
        mo.addAttribute("currentPage", page);
        mo.addAttribute("totalPages", totalPages);

        return "QnA/UserQnAOut";
    }

}
