import React, { useState } from "react";
import styles from "./MultiMenu.module.css";

export const MultiMenus = ({ menus }) => {
    const [activeMenus, setActiveMenus] = useState([]);
    const [isMenuOpen, setIsMenuOpen] = useState(false);

    const handleMenuClick = (data) => {
        console.log("Menu clicked:", data);
    };

    const handleArrowClick = (menuName) => {
        setActiveMenus((prev) =>
            prev.includes(menuName)
                ? prev.filter((name) => name !== menuName)
                : [...prev, menuName]
        );
    };

    const ListMenu = ({ dept, data, hasSubMenu, menuName, menuIndex }) => {
        const isOpen = activeMenus.includes(menuName);
        return (
            <li className={styles.multiMenuLi}>
                <div
                    className={styles.multiMenuItem}
                    style={{ paddingLeft: `${dept * 18}px` }}
                >
                    <span
                        className={styles.multiMenuLabel}
                        onClick={() => handleMenuClick(data)}
                    >
                        {data.label}
                    </span>
                    {hasSubMenu && (
                        <span
                            className={`${styles.multiMenuArrow} ${isOpen ? styles.toggle : ""}`}
                            onClick={() => handleArrowClick(menuName)}
                        />
                    )}
                </div>
                {hasSubMenu && isOpen && (
                    <SubMenu
                        dept={dept + 1}
                        data={data.submenu}
                        menuIndex={menuIndex}
                    />
                )}
            </li>
        );
    };

    const SubMenu = ({ dept, data, menuIndex }) => {
        return (
            <ul className={styles.multiMenuUl}>
                {data.map((menu, index) => {
                    const menuName = `submenu-${dept}-${menuIndex}-${index}`;
                    const hasSub = Array.isArray(menu.submenu) && menu.submenu.length > 0;
                    return (
                        <ListMenu
                            key={menuName}
                            dept={dept}
                            data={menu}
                            hasSubMenu={hasSub}
                            menuName={menuName}
                            menuIndex={index}
                        />
                    );
                })}
            </ul>
        );
    };

    return (
        <div>
            <button onClick={() => setIsMenuOpen(true)}>Open Menu</button>

            {isMenuOpen && (
                <div>
                    <div className={styles.backdrop} onClick={() => setIsMenuOpen(false)} />
                    <div className={styles.menuOverlay}>
                        <ul className={styles.multiMenuUl}>
                            {menus[0].submenu.map((menu, index) => {
                                const dept = 1;
                                const menuName = `menu-${dept}-${index}`;
                                const hasSub = Array.isArray(menu.submenu) && menu.submenu.length > 0;
                                return (
                                    <ListMenu
                                        key={menuName}
                                        dept={dept}
                                        data={menu}
                                        hasSubMenu={hasSub}
                                        menuName={menuName}
                                        menuIndex={index}
                                    />
                                );
                            })}
                        </ul>
                    </div>
                </div>
            )}
        </div>
    );
};
