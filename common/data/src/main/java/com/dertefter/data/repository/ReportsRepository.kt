package com.dertefter.data.repository

import com.dertefter.data.dto.reports.ReportDataDto

interface ReportsRepository {
    suspend fun createReport(
        targetType: String,
        targetId: String,
        reason: String,
        description: String?
    ): Result<ReportDataDto>
}

/*

Примеры запроса:
{"targetType":"post","targetId":"4854f9af-12d8-4e28-9273-86c281e56753","reason":"spam","description":"мими"}

Пример ответа:
{
    "data": {
        "id": "f06ff505-c399-4b55-ba95-cbe05f965d81",
        "createdAt": "2026-09-12T06:04:04.450Z"
    }
}


реализация ui в браузере:
import { a6, a7, d, u, B as B_1, M, x as x_1 } from "./index-gHxZkwFX.js";
import { a7 as a7_1 } from "./IconCheckCircle-CdAOfIvt.js";
(() => {
  try {
    const a =
      typeof window !== "undefined"
        ? window
        : typeof global !== "undefined"
        ? global
        : typeof globalThis !== "undefined"
        ? globalThis
        : typeof self !== "undefined"
        ? self
        : {};
    a.SENTRY_RELEASE = { id: "1.1.4" };
    const i = new a.Error().stack;

    if (i) {
      a._sentryDebugIds = a._sentryDebugIds || {};
      a._sentryDebugIds[i] = "94ee3101-5f89-4b3f-9876-c46bb68210b4";
      a._sentryDebugIdIdentifier =
        "sentry-dbid-94ee3101-5f89-4b3f-9876-c46bb68210b4";
    }
  } catch {}
})();

const l = {
  TARGET_NOT_FOUND: "REPORT_TARGET_NOT_FOUND",
  CANNOT_REPORT_OWN_CONTENT: "REPORT_CANNOT_REPORT_OWN_CONTENT",
  ALREADY_REPORTED: "REPORT_ALREADY_REPORTED",
  DESCRIPTION_TOO_LONG: "REPORT_DESCRIPTION_TOO_LONG",
};

const P = {
  async createReport(a) {
    const i = await a6.post(a7.reports.create, a);
    return i?.data ?? i;
  },
};

const y = "l8SU";
const D = "ALNW";
const S = "eEGY";
const g = "S3vG";
const C = "EYxO";
const w = "ymhm";
const k = "BBIZ";
const x = "vHkx";
const L = "qREi";
const G = "PTkt";
const Y = "Ih6N";
const B = "lXwP";
const F = "HAOc";
const H = "rEQx";

const s = {
  modalReport: y,
  content: D,
  successIcon: S,
  title: g,
  subtitle: C,
  options: w,
  chip: k,
  radio: x,
  chipActive: L,
  radioDot: G,
  detailsSection: Y,
  textarea: B,
  error: F,
  actions: H,
};

const U = {
  spam: "spam",
  violence: "violence",
  hate: "harassment",
  adult: "nudity",
  misinfo: "misinformation",
  other: "other",
};

const W = [
  { id: "spam", label: "Спам или нежелательный контент" },
  { id: "violence", label: "Насилие или опасные действия" },
  { id: "hate", label: "Ненависть или травля" },
  { id: "adult", label: "Контент для взрослых (18+)" },
  { id: "misinfo", label: "Дезинформация или обман" },
  { id: "other", label: "Другое" },
];

export function ReportModal({ targetType, targetId, onClose, onSubmit }) {
  const [r, u] = d(null);
  const [d, T] = d("");
  const [N, m] = d(false);
  const [O, c] = d(null);
  const [E, b] = d(false);

  const f = async () => {
    if (r) {
      m(true);
      c(null);
      try {
        if (onSubmit) {
          await onSubmit(r, d);
        } else {
          await P.createReport({
            targetType: targetType,
            targetId: targetId,
            reason: U[r],
            description: d || undefined,
          });
        }

        b(true);
      } catch (t) {
        console.error("Failed to submit report:", t);

        if (x_1(t)) {
          switch (t.code) {
            case l.CANNOT_REPORT_OWN_CONTENT: {
              c("Вы не можете пожаловаться на свой контент");
              break;
            }
            case l.ALREADY_REPORTED: {
              c("Вы уже отправляли жалобу на этот контент");
              break;
            }
            case l.TARGET_NOT_FOUND: {
              c("Контент не найден");
              break;
            }
            case l.DESCRIPTION_TOO_LONG: {
              c("Описание слишком длинное (макс. 1000 символов)");
              break;
            }
            default: {
              c("Произошла ошибка при отправке жалобы");
            }
          }
        } else {
          c("Произошла ошибка при отправке жалобы");
        }
      } finally {
        m(false);
      }
    }
  };

  return E
    ? u(M, {
        onClose: onClose,
        showHeader: false,
        frameless: false,
        className: s.modalReport,
        children: u("div", {
          className: s.content,
          children: [
            u("div", {
              className: s.successIcon,
              children: u(a7_1, { size: 48 }),
            }),
            u("h2", { className: s.title, children: "Спасибо за жалобу!" }),
            u("p", {
              className: s.subtitle,
              children:
                "Мы рассмотрим вашу жалобу и примем необходимые меры. Вы помогаете сделать сообщество лучше.",
            }),
            u("div", {
              className: s.actions,
              children: u(B_1, {
                variant: "primary",
                onClick: (t) => {
                  t.stopPropagation();
                  onClose();
                },
                children: "Понятно",
              }),
            }),
          ],
        }),
      })
    : u(M, {
        onClose: onClose,
        showHeader: false,
        frameless: false,
        className: s.modalReport,
        children: u("div", {
          className: s.content,
          children: [
            u("h2", { className: s.title, children: "Пожаловаться" }),
            u("p", {
              className: s.subtitle,
              children: "Выберите причину жалобы",
            }),
            u("div", {
              className: s.options,
              children: W.map((t) =>
                u(
                  "button",
                  {
                    type: "button",
                    className: `${s.chip} ${r === t.id ? s.chipActive : ""}`,
                    onClick: () => u(t.id),
                    children: [
                      u("span", {
                        className: s.radio,
                        children:
                          r === t.id && u("span", { className: s.radioDot }),
                      }),
                      t.label,
                    ],
                  },
                  t.id
                )
              ),
            }),
            u("div", {
              className: s.detailsSection,
              children: u("textarea", {
                className: s.textarea,
                placeholder: "Опишите подробнее (необязательно)...",
                value: d,
                onInput: (t) => T(t.target.value),
                rows: 3,
                maxLength: 1000 /* 1e3 */,
              }),
            }),
            O && u("div", { className: s.error, children: O }),
            u("div", {
              className: s.actions,
              children: [
                u(B_1, {
                  variant: "secondary",
                  onClick: (t) => {
                    t.stopPropagation();
                    onClose();
                  },
                  disabled: N,
                  children: "Отмена",
                }),
                u(B_1, {
                  variant: "primary",
                  onClick: (t) => {
                    t.stopPropagation();
                    f();
                  },
                  disabled: !r || N,
                  children: N ? "Отправка..." : "Отправить",
                }),
              ],
            }),
          ],
        }),
      });
}

export { ReportModal as ReportModal, ReportModal as default };

 */