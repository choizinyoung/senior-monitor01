/* 모바일 사이드바 드로어 */
(function () {
    var sidebar, backdrop, hamburger;

    function init() {
        sidebar = document.querySelector('.sidebar');
        if (!sidebar) return;

        // 백드롭 생성
        backdrop = document.createElement('div');
        backdrop.className = 'sidebar-backdrop';
        backdrop.addEventListener('click', close);
        document.body.appendChild(backdrop);

        // 햄버거 버튼 생성
        hamburger = document.createElement('button');
        hamburger.className = 'hamburger';
        hamburger.setAttribute('aria-label', '메뉴 열기');
        hamburger.innerHTML = '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" viewBox="0 0 24 24"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>';
        hamburger.addEventListener('click', toggle);

        var topbar = document.querySelector('.topbar');
        if (topbar) topbar.insertBefore(hamburger, topbar.firstChild);

        // 모바일에서 nav-item 클릭 시 자동 닫기
        sidebar.querySelectorAll('.nav-item').forEach(function (item) {
            item.addEventListener('click', function () {
                if (window.innerWidth <= 768) close();
            });
        });

        // ESC 키로 닫기
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') close();
        });
    }

    function toggle() {
        sidebar.classList.contains('open') ? close() : open();
    }

    function open() {
        sidebar.classList.add('open');
        backdrop.classList.add('active');
        document.body.classList.add('sidebar-is-open');
        hamburger.setAttribute('aria-label', '메뉴 닫기');
        hamburger.innerHTML = '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" viewBox="0 0 24 24"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>';
    }

    function close() {
        sidebar.classList.remove('open');
        backdrop.classList.remove('active');
        document.body.classList.remove('sidebar-is-open');
        hamburger.setAttribute('aria-label', '메뉴 열기');
        hamburger.innerHTML = '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" viewBox="0 0 24 24"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>';
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();

/* 테이블 → 모바일 카드 변환용 data-label 자동 주입 */
(function () {
    function injectDataLabels() {
        document.querySelectorAll('table').forEach(function (table) {
            var headers = [];
            table.querySelectorAll('thead th').forEach(function (th) {
                headers.push(th.textContent.trim());
            });
            if (!headers.length) return;

            table.querySelectorAll('tbody tr').forEach(function (tr) {
                var cells = tr.querySelectorAll('td');
                cells.forEach(function (td, i) {
                    if (headers[i]) td.setAttribute('data-label', headers[i]);
                });
                // 버튼이 있는 마지막 셀만 액션 영역으로 표시
                var last = cells[cells.length - 1];
                if (last && last.querySelector('button, a.btn, .btn-text')) {
                    last.classList.add('td-action');
                }
            });
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', injectDataLabels);
    } else {
        injectDataLabels();
    }
})();
