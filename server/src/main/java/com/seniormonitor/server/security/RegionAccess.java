package com.seniormonitor.server.security;

import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.exception.ForbiddenException;

public final class RegionAccess {

    private RegionAccess() {
    }

    /** MASTER가 아니면서 시/구 모두 미배정인 담당자 (시만 배정된 경우는 유효한 배정으로 간주) */
    public static boolean isUnassigned(CurrentManager manager) {
        return !manager.isMaster() && manager.city() == null && manager.gu() == null;
    }

    /** 목록 조회 시 사용할 city 필터값. MASTER는 null(전체 조회) */
    public static String cityFilter(CurrentManager manager) {
        return manager.isMaster() ? null : manager.city();
    }

    /** 목록 조회 시 사용할 gu 필터값. MASTER는 null(전체 조회) */
    public static String guFilter(CurrentManager manager) {
        return manager.isMaster() ? null : manager.gu();
    }

    /** 목록 조회 시 사용할 dong 필터값. MASTER는 null(전체 조회) */
    public static String dongFilter(CurrentManager manager) {
        return manager.isMaster() ? null : manager.dong();
    }

    /** manager가 null이면 로그인 없이 호출된 것(APK 등 공개 엔드포인트)이므로 지역 제한을 적용하지 않는다. */
    public static void assertAccessible(Senior senior, CurrentManager manager) {
        if (manager == null || manager.isMaster()) {
            return;
        }
        boolean guMatch = manager.gu() != null && manager.gu().equals(senior.getGu());
        boolean dongMatch = manager.dong() == null || manager.dong().equals(senior.getDong());
        if (!guMatch || !dongMatch) {
            throw new ForbiddenException("ERR_FORBIDDEN_REGION", "관할 지역 밖의 대상자입니다.");
        }
    }
}
