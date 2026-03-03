document.addEventListener('DOMContentLoaded', function() {
    // ========== 主题切换功能 ==========
    const toggleSwitch = document.querySelector('.theme-switch input[type="checkbox"]');
    const currentTheme = localStorage.getItem('theme') || 'dark';

    document.documentElement.setAttribute('data-theme', currentTheme);
    if (currentTheme === 'dark' && toggleSwitch) {
        toggleSwitch.checked = true;
    }

    function switchTheme(e) {
        if (e.target.checked) {
            document.documentElement.setAttribute('data-theme', 'dark');
            localStorage.setItem('theme', 'dark');
        } else {
            document.documentElement.setAttribute('data-theme', 'light');
            localStorage.setItem('theme', 'light');
        }
    }

    if (toggleSwitch) {
        toggleSwitch.addEventListener('change', switchTheme);
    }

    // ========== 首页数据驱动渲染 ==========
    async function loadHomeData() {
        try {
            const response = await fetch('data/home.json');
            if (!response.ok) {
                throw new Error('无法加载配置数据');
            }
            const data = await response.json();
            renderHome(data);
        } catch (error) {
            console.error('加载首页数据失败:', error);
        }
    }

    function renderHome(data) {
        // Hero
        if (data.hero) {
            const heroName = document.getElementById('hero-name');
            const heroTagline = document.getElementById('hero-tagline');
            const heroAvatar = document.getElementById('hero-avatar');

            if (heroName) heroName.textContent = data.hero.name;
            if (heroTagline) heroTagline.textContent = data.hero.tagline;
            if (heroAvatar && data.hero.avatar) heroAvatar.src = data.hero.avatar;
        }

        // Now/Status
        if (data.now) {
            const nowSummary = document.getElementById('now-summary');
            const nowUpdated = document.getElementById('now-updated');
            const nowLink = document.getElementById('now-link');

            if (nowSummary) nowSummary.textContent = data.now.summary;
            if (nowUpdated) nowUpdated.textContent = `更新于 ${data.now.updated}`;
            if (nowLink) nowLink.href = data.now.detailLink;
        }

        // Navigation links
        if (data.navigation) {
            const projectsAllLink = document.getElementById('projects-all-link');
            const notesAllLink = document.getElementById('notes-all-link');
            const navGarden = document.getElementById('nav-garden');

            if (projectsAllLink) projectsAllLink.href = data.navigation.projectsListUrl;
            if (notesAllLink) notesAllLink.href = data.navigation.notesListUrl;
            if (navGarden) navGarden.href = data.navigation.gardenUrl;
        }

        // Featured Projects
        if (data.featuredProjects && data.featuredProjects.length > 0) {
            renderProjects(data.featuredProjects);
        }

        // Recent Notes
        if (data.recentNotes && data.recentNotes.length > 0) {
            renderNotes(data.recentNotes);
        }

        // Contact
        if (data.contact) {
            renderContact(data.contact);
        }

        // Footer
        if (data.footer) {
            const footerCopyright = document.getElementById('footer-copyright');
            const footerQuote = document.getElementById('footer-quote');

            if (footerCopyright) {
                footerCopyright.innerHTML = `&copy; ${data.footer.copyright} · <a href="${data.footer.githubUrl}" target="_blank">GitHub</a>`;
            }
            if (footerQuote) footerQuote.textContent = data.footer.quote;
        }
    }

    function renderProjects(projects) {
        const container = document.getElementById('projects-grid');
        if (!container) return;

        container.innerHTML = projects.map(project => {
            const statusClass = getStatusClass(project.status);
            const statusTitle = getStatusTitle(project.status);
            const tagsHtml = project.tags.map(tag => `<span class="card-tag">${tag}</span>`).join('');

            return `
                <a href="${project.link}" class="home-card" target="_blank" rel="noopener noreferrer">
                    <div class="card-header">
                        <h3>${project.name}</h3>
                        <span class="status-dot ${statusClass}" title="${statusTitle}"></span>
                    </div>
                    <p class="card-desc">${project.description}</p>
                    <div class="card-footer">
                        ${tagsHtml}
                    </div>
                </a>
            `;
        }).join('');
    }

    function renderNotes(notes) {
        const container = document.getElementById('notes-list');
        if (!container) return;

        container.innerHTML = notes.map(note => `
            <a href="${note.link}" class="note-item" target="_blank" rel="noopener noreferrer">
                <h3>${note.title}</h3>
                <p>${note.summary}</p>
                <span class="note-date">${note.date}</span>
            </a>
        `).join('');
    }

    function renderContact(contact) {
        const container = document.getElementById('footer-contact');
        if (!container) return;

        let items = [];

        if (contact.wechat) {
            items.push(`<span>微信: ${contact.wechat}</span>`);
        }

        if (contact.publicAccount) {
            items.push(`<span>公众号: ${contact.publicAccount}</span>`);
        }

        if (contact.github) {
            items.push(`<a href="${contact.github}" target="_blank" rel="noopener noreferrer">GitHub</a>`);
        }

        container.innerHTML = items.join('<span style="opacity:0.3">·</span>');
    }

    function getStatusClass(status) {
        const statusMap = {
            'online': 'status-online',
            'github': 'status-github',
            'building': 'status-building'
        };
        return statusMap[status] || 'status-github';
    }

    function getStatusTitle(status) {
        const titleMap = {
            'online': '已上线',
            'github': '仅 GitHub',
            'building': '建设中'
        };
        return titleMap[status] || '状态未知';
    }

    // 只在首页加载数据（检查是否存在首页特有的元素）
    if (document.getElementById('projects-grid')) {
        loadHomeData();
    }
});
