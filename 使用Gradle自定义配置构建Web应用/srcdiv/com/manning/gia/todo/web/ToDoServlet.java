package com.manning.gia.todo.web;

import com.manning.gia.todo.model.ToDoItem;
import com.manning.gia.todo.repository.InMemoryToDoRepository;
import com.manning.gia.todo.repository.ToDoRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ToDoServlet extends HttpServlet {
    public static final String FIND_ALL_SERVLET_PATH = "/all";
    public static final String INDEX_PAGE = "/jsp/todo-list.jsp";
    private ToDoRepository toDoRepository = new InMemoryToDoRepository();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取 Servlet 访问路径 【请求的 URL 路径，以 / 开头】
        String servletPath = request.getServletPath();
        //获取视图
        String view = processRequest(servletPath, request);
        //跳转到对应视图
        RequestDispatcher dispatcher = request.getRequestDispatcher(view);
        dispatcher.forward(request, response);   //Servlet --> jsp
    }

    private String processRequest(String servletPath, HttpServletRequest request) {
        //根据不同路径进入不同方法，跳转不同页面
        if (servletPath.equals(FIND_ALL_SERVLET_PATH)) {
            List<ToDoItem> toDoItems = toDoRepository.findAll();
            request.setAttribute("toDoItems", toDoItems);
            request.setAttribute("stats", determineStats(toDoItems));
            request.setAttribute("filter", "all");
            return INDEX_PAGE;
        } else if (servletPath.equals("/active")) {
            List<ToDoItem> toDoItems = toDoRepository.findAll();
            request.setAttribute("toDoItems", filterBasedOnStatus(toDoItems, true));
            request.setAttribute("stats", determineStats(toDoItems));
            request.setAttribute("filter", "active");
            return INDEX_PAGE;
        } else if (servletPath.equals("/completed")) {
            List<ToDoItem> toDoItems = toDoRepository.findAll();
            request.setAttribute("toDoItems", filterBasedOnStatus(toDoItems, false));
            request.setAttribute("stats", determineStats(toDoItems));
            request.setAttribute("filter", "completed");
            return INDEX_PAGE;
        }
        if (servletPath.equals("/insert")) {
            ToDoItem toDoItem = new ToDoItem();
            toDoItem.setName(request.getParameter("name"));
            toDoRepository.insert(toDoItem);
            return "/" + request.getParameter("filter");
        } else if (servletPath.equals("/update")) {
            ToDoItem toDoItem = toDoRepository.findById(Long.parseLong(request.getParameter("id")));

            if (toDoItem != null) {
                toDoItem.setName(request.getParameter("name"));
                toDoRepository.update(toDoItem);
            }

            return "/" + request.getParameter("filter");
        } else if (servletPath.equals("/delete")) {
            ToDoItem toDoItem = toDoRepository.findById(Long.parseLong(request.getParameter("id")));

            if (toDoItem != null) {
                toDoRepository.delete(toDoItem);
            }

            return "/" + request.getParameter("filter");
        } else if (servletPath.equals("/toggleStatus")) {
            ToDoItem toDoItem = toDoRepository.findById(Long.parseLong(request.getParameter("id")));

            if (toDoItem != null) {
                boolean completed = "on".equals(request.getParameter("toggle")) ? true : false;
                toDoItem.setCompleted(completed);
                toDoRepository.update(toDoItem);
            }

            return "/" + request.getParameter("filter");
        } else if (servletPath.equals("/clearCompleted")) {
            List<ToDoItem> toDoItems = toDoRepository.findAll();

            for (ToDoItem toDoItem : toDoItems) {
                if (toDoItem.isCompleted()) {
                    toDoRepository.delete(toDoItem);
                }
            }

            return "/" + request.getParameter("filter");
        }
        //没有匹配路径则跳转到 /all
        return FIND_ALL_SERVLET_PATH;
    }

    private List<ToDoItem> filterBasedOnStatus(List<ToDoItem> toDoItems, boolean active) {
        List<ToDoItem> filteredToDoItems = new ArrayList<ToDoItem>();

        for (ToDoItem toDoItem : toDoItems) {
            if (toDoItem.isCompleted() != active) {
                filteredToDoItems.add(toDoItem);
            }
        }

        return filteredToDoItems;
    }

    private ToDoListStats determineStats(List<ToDoItem> toDoItems) {
        ToDoListStats toDoListStats = new ToDoListStats();

        for (ToDoItem toDoItem : toDoItems) {
            if (toDoItem.isCompleted()) {
                toDoListStats.addCompleted();
            } else {
                toDoListStats.addActive();
            }
        }

        return toDoListStats;
    }

    public class ToDoListStats {
        private int active;
        private int completed;

        private void addActive() {
            active++;
        }

        private void addCompleted() {
            completed++;
        }

        public int getActive() {
            return active;
        }

        public int getCompleted() {
            return completed;
        }

        public int getAll() {
            return active + completed;
        }
    }
}